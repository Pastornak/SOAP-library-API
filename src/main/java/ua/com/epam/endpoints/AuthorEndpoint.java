package ua.com.epam.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ua.com.epam.entities.*;
import ua.com.epam.entity.Author;
import ua.com.epam.exception.author.AuthorAlreadyExistsException;
import ua.com.epam.exception.author.AuthorHasBooksException;
import ua.com.epam.exception.author.AuthorNotFoundException;
import ua.com.epam.exception.book.BookNotFoundException;
import ua.com.epam.exception.genre.GenreNotFoundException;
import ua.com.epam.repository.AuthorRepository;
import ua.com.epam.repository.BookRepository;
import ua.com.epam.repository.GenreRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ua.com.epam.WebServiceConfig.WEB_SERVICE_NAMESPACE;

@Endpoint
public class AuthorEndpoint {

    private static final String NAMESPACE_URI = WEB_SERVICE_NAMESPACE;
    private static final String DEFAULT_SORT_BY = "authorId";

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAuthorRequest")
    @ResponsePayload
    public GetAuthorResponse getAuthor(@RequestPayload GetAuthorRequest request) {
        long authorId = request.getAuthorId();
        Author author = this.authorRepository.getOneByAuthorId(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));

        GetAuthorResponse response = new GetAuthorResponse();
        response.setAuthor(mapAuthorToAuthorType(author));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAuthorsRequest")
    @ResponsePayload
    public GetAuthorsResponse getAuthors(@RequestPayload GetAuthorsRequest request) {
        ParamsValidator.validateSearchParams(request.getSearch());
        Sort.Direction direction = Sort.Direction.fromString(request.getSearch().getOrderType());
        Sort sorter = Sort.by(direction, DEFAULT_SORT_BY);

        List<Author> rawAuthors;

        if (!request.getSearch().isPagination()) {
            rawAuthors = authorRepository.findAll(sorter);
        } else {
            rawAuthors = authorRepository.getAllAuthors(PageRequest.of(request.getSearch().getPage().intValue() - 1, request.getSearch().getSize(), sorter));
        }

        GetAuthorsResponse response = new GetAuthorsResponse();
        Authors responseAuthors = new Authors();
        Collection<AuthorType> mappedAuthors = rawAuthors.stream().map(this::mapAuthorToAuthorType).collect(Collectors.toList());
        responseAuthors.getAuthor().clear();
        responseAuthors.getAuthor().addAll(mappedAuthors);
        response.setAuthors(responseAuthors);

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createAuthorRequest")
    @ResponsePayload
    public CreateAuthorResponse createAuthor(@RequestPayload CreateAuthorRequest request) {
        if (authorRepository.existsByAuthorId(request.getAuthor().getAuthorId())) {
            throw new AuthorAlreadyExistsException(request.getAuthor().getAuthorId());
        }

        Author createdAuthor = mapAuthorTypeToAuthor(request.getAuthor());
        Author savedAuthor = authorRepository.save(createdAuthor);

        CreateAuthorResponse response = new CreateAuthorResponse();
        response.setAuthor(mapAuthorToAuthorType(savedAuthor));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateAuthorRequest")
    @ResponsePayload
    public UpdateAuthorResponse updateAuthor(@RequestPayload UpdateAuthorRequest request) {
        Author proxy = authorRepository.getOneByAuthorId(request.getAuthor().getAuthorId())
                .orElseThrow(() -> new AuthorNotFoundException(request.getAuthor().getAuthorId()));

        Author newInfo = mapAuthorTypeToAuthor(request.getAuthor());

        proxy.setFirstName(newInfo.getFirstName());
        proxy.setSecondName(newInfo.getSecondName());
        proxy.setDescription(newInfo.getDescription());
        proxy.setNationality(newInfo.getNationality());
        proxy.setBirthDate(newInfo.getBirthDate());
        proxy.setBirthCity(newInfo.getBirthCity());
        proxy.setBirthCountry(newInfo.getBirthCountry());

        Author updatedAuthor = authorRepository.save(proxy);

        UpdateAuthorResponse response = new UpdateAuthorResponse();
        response.setAuthor(mapAuthorToAuthorType(updatedAuthor));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteAuthorRequest")
    @ResponsePayload
    public DeleteAuthorResponse deleteAuthor(@RequestPayload DeleteAuthorRequest request) {
        long authorId = request.getAuthorId();
        Author toDelete = authorRepository.getOneByAuthorId(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));

        long bookCount = bookRepository.getAllBooksOfAuthorCount(authorId);

        if (bookCount > 0 && !request.getOptions().isForcibly()) {
            throw new AuthorHasBooksException(authorId);
        }

        authorRepository.delete(toDelete);

        DeleteAuthorResponse response = new DeleteAuthorResponse();
        response.setStatus("Successfully deleted author " + authorId);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchAuthorsRequest")
    @ResponsePayload
    public SearchAuthorsResponse searchAuthors(@RequestPayload SearchAuthorsRequest request) {
        ParamsValidator.validateSearchParams(request.getSearch());
        String sortOrder = request.getSearch().getOrderType();
        int size = request.getSearch().getSize();
        String query = request.getQuery();

        String wordRegex = "\\S+";

        Matcher wordMatcher = Pattern.compile(wordRegex)
                .matcher(query);
        List<String> queryWords = new ArrayList<>();
        while(wordMatcher.find()) {
            queryWords.add(wordMatcher.group().toLowerCase());
        }

        Sort sorter = Sort.by(Sort.Direction.fromString(sortOrder), DEFAULT_SORT_BY);
        List<Author> rawAuthors = authorRepository.findAll(sorter).stream().filter(author -> {
            // intentionally leaving such basic algorithm for 2 reasons:
            // 1) presenting it as a potential bug
            // 2) too lazy to think of something smart
            String name = author.getFirstName().toLowerCase();
            if (queryWords.stream().anyMatch(name::startsWith)) {
                return true;
            }
            String surname = author.getSecondName().toLowerCase();
            if (queryWords.stream().anyMatch(surname::startsWith)) {
                return true;
            }
            return false;
        }).limit(size).collect(Collectors.toList());

        SearchAuthorsResponse response = new SearchAuthorsResponse();
        Authors responseAuthors = new Authors();
        Collection<AuthorType> mappedAuthors = rawAuthors.stream().map(this::mapAuthorToAuthorType).collect(Collectors.toList());
        responseAuthors.getAuthor().clear();
        responseAuthors.getAuthor().addAll(mappedAuthors);
        response.setAuthors(responseAuthors);

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAuthorByBookRequest")
    @ResponsePayload
    public GetAuthorByBookResponse getAuthorByBook(@RequestPayload GetAuthorByBookRequest request) {
        long bookId = request.getBookId();
        if (!bookRepository.existsByBookId(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        Author author = authorRepository.getAuthorOfBook(bookId);
        GetAuthorByBookResponse response = new GetAuthorByBookResponse();
        response.setAuthor(mapAuthorToAuthorType(author));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAuthorsByGenreRequest")
    @ResponsePayload
    public GetAuthorsByGenreResponse getAuthorsByGenre(@RequestPayload GetAuthorsByGenreRequest request) {
        long genreId = request.getGenreId();
        if (!genreRepository.existsByGenreId(genreId)) {
            throw new GenreNotFoundException(genreId);
        }

        Sort sorter = Sort.by(Sort.Direction.fromString(request.getSearch().getOrderType()), DEFAULT_SORT_BY);

        List<Author> rawAuthors;

        if (!request.getSearch().isPagination()) {
            rawAuthors = authorRepository.getAllAuthorsInGenre(genreId, sorter);
        } else {
            rawAuthors = authorRepository.getAllAuthorsInGenre(genreId, PageRequest.of(request.getSearch().getPage().intValue() - 1, request.getSearch().getSize(), sorter));
        }

        GetAuthorsByGenreResponse response = new GetAuthorsByGenreResponse();
        Authors responseAuthors = new Authors();
        Collection<AuthorType> mappedAuthors = rawAuthors.stream().map(this::mapAuthorToAuthorType).collect(Collectors.toList());
        responseAuthors.getAuthor().clear();
        responseAuthors.getAuthor().addAll(mappedAuthors);
        response.setAuthors(responseAuthors);

        return response;
    }

    private AuthorType mapAuthorToAuthorType(Author author) {
        AuthorType at = new AuthorType();
        at.setAuthorId(author.getAuthorId());
        at.setAuthorDescription(author.getDescription());
        at.setNationality(author.getNationality());
        AuthorType.AuthorName atName = new AuthorType.AuthorName();
        atName.setFirst(author.getFirstName());
        atName.setSecond(author.getSecondName());
        at.setAuthorName(atName);
        AuthorType.Birth atBirth = new AuthorType.Birth();
        atBirth.setCity(author.getBirthCity());
        atBirth.setCountry(author.getBirthCountry());
        atBirth.setDate(author.getBirthDate().toString());
        at.setBirth(atBirth);
        return at;
    }

    private Author mapAuthorTypeToAuthor(AuthorType authorType) {
        Author author = new Author();
        author.setAuthorId(authorType.getAuthorId());
        author.setDescription(authorType.getAuthorDescription());
        author.setNationality(authorType.getNationality());
        author.setFirstName(authorType.getAuthorName().getFirst());
        author.setSecondName(authorType.getAuthorName().getSecond());
        author.setBirthCity(authorType.getBirth().getCity());
        author.setBirthCountry(authorType.getBirth().getCountry());
        author.setBirthDate(LocalDate.parse(authorType.getBirth().getDate()));
        return author;
    }
}
