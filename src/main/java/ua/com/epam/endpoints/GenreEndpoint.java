package ua.com.epam.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ua.com.epam.entities.*;
import ua.com.epam.entity.Genre;
import ua.com.epam.exception.author.AuthorNotFoundException;
import ua.com.epam.exception.book.BookNotFoundException;
import ua.com.epam.exception.genre.GenreAlreadyExistsException;
import ua.com.epam.exception.genre.GenreHasBooksException;
import ua.com.epam.exception.genre.GenreNameAlreadyExistsException;
import ua.com.epam.exception.genre.GenreNotFoundException;
import ua.com.epam.repository.AuthorRepository;
import ua.com.epam.repository.BookRepository;
import ua.com.epam.repository.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ua.com.epam.WebServiceConfig.WEB_SERVICE_NAMESPACE;

@Endpoint
public class GenreEndpoint {

    private static final String NAMESPACE_URI = WEB_SERVICE_NAMESPACE;
    private static final String DEFAULT_SORT_BY = "genreId";

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getGenreRequest")
    @ResponsePayload
    public GetGenreResponse getGenre(@RequestPayload GetGenreRequest request) {
        Genre genre = genreRepository.getOneByGenreId(request.getGenreId())
                .orElseThrow(() -> new GenreNotFoundException(request.getGenreId()));

        GetGenreResponse response = new GetGenreResponse();
        response.setGenre(mapGenreToGenreType(genre));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getGenresRequest")
    @ResponsePayload
    public GetGenresResponse getGenres(@RequestPayload GetGenresRequest request) {
        ParamsValidator.validateSearchParams(request.getSearch());
        Sort.Direction direction = Sort.Direction.fromString(request.getSearch().getOrderType());
        Sort sorter = Sort.by(direction, DEFAULT_SORT_BY);

        List<Genre> rawGenres;

        if (!request.getSearch().isPagination()) {
            rawGenres = genreRepository.findAll(sorter);
        } else {
            rawGenres = genreRepository.getAllGenres(PageRequest.of(request.getSearch().getPage().intValue() - 1, request.getSearch().getSize(), sorter));
        }

        GetGenresResponse response = new GetGenresResponse();
        Genres responseGenres = new Genres();
        List<GenreType> mappedGenres = rawGenres.stream().map(this::mapGenreToGenreType).collect(Collectors.toList());
        responseGenres.getGenre().clear();
        responseGenres.getGenre().addAll(mappedGenres);
        response.setGenres(responseGenres);

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createGenreRequest")
    @ResponsePayload
    public CreateGenreResponse createGenre(@RequestPayload CreateGenreRequest request) {
        if (genreRepository.existsByGenreId(request.getGenre().getGenreId())) {
            throw new GenreAlreadyExistsException(request.getGenre().getGenreId());
        }

        if (genreRepository.existsByGenreName(request.getGenre().getGenreName())) {
            throw new GenreNameAlreadyExistsException(request.getGenre().getGenreName());
        }

        Genre createdGenre = mapGenreTypeToGenre(request.getGenre());
        Genre savedGenre = genreRepository.save(createdGenre);

        CreateGenreResponse response = new CreateGenreResponse();
        response.setGenre(mapGenreToGenreType(savedGenre));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateGenreRequest")
    @ResponsePayload
    public UpdateGenreResponse updateGenre(@RequestPayload UpdateGenreRequest request) {
        Genre proxy = genreRepository.getOneByGenreId(request.getGenre().getGenreId())
                .orElseThrow(() -> new GenreNotFoundException(request.getGenre().getGenreId()));

        proxy.setGenreName(request.getGenre().getGenreName());
        proxy.setDescription(request.getGenre().getGenreDescription());

        Genre updatedGenre = genreRepository.save(proxy);

        UpdateGenreResponse response = new UpdateGenreResponse();
        response.setGenre(mapGenreToGenreType(updatedGenre));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteGenreRequest")
    @ResponsePayload
    public DeleteGenreResponse deleteGenre(@RequestPayload DeleteGenreRequest request) {
        long genreId = request.getGenreId();
        Genre toDelete = genreRepository.getOneByGenreId(genreId)
                .orElseThrow(() -> new GenreNotFoundException(genreId));

        long bookCount = bookRepository.getAllBooksInGenreCount(genreId);

        if (bookCount > 0 && !request.getOptions().isForcibly()) {
            throw new GenreHasBooksException(genreId);
        }

        genreRepository.delete(toDelete);

        DeleteGenreResponse response = new DeleteGenreResponse();
        response.setStatus("Successfully deleted genre " + genreId);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchGenresRequest")
    @ResponsePayload
    public SearchGenresResponse searchGenres(@RequestPayload SearchGenresRequest request) {
        ParamsValidator.validateSearchParams(request.getSearch());
        String sortOrder = request.getSearch().getOrderType();
        int size = request.getSearch().getSize();
        // we expect to get one word
        String query = request.getQuery().trim().toLowerCase();

        Sort sorter = Sort.by(Sort.Direction.fromString(sortOrder), DEFAULT_SORT_BY);
        List<Genre> rawGenres = genreRepository.findAll(sorter).stream()
                .filter(genre -> genre.getGenreName().toLowerCase().contains(query))
                .limit(size)
                .collect(Collectors.toList());

        SearchGenresResponse response = new SearchGenresResponse();
        Genres responseGenres = new Genres();
        List<GenreType> mappedGenres = rawGenres.stream().map(this::mapGenreToGenreType).collect(Collectors.toList());
        responseGenres.getGenre().clear();
        responseGenres.getGenre().addAll(mappedGenres);
        response.setGenres(responseGenres);

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getGenreByBookRequest")
    @ResponsePayload
    public GetGenreByBookResponse getGenreByBook(@RequestPayload GetGenreByBookRequest request) {
        long bookId = request.getBookId();
        if (!bookRepository.existsByBookId(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        Genre genre = genreRepository.getGenreOfBook(bookId);

        GetGenreByBookResponse response = new GetGenreByBookResponse();
        response.setGenre(mapGenreToGenreType(genre));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getGenresByAuthorRequest")
    @ResponsePayload
    public GetGenresByAuthorResponse getGenresByAuthor(@RequestPayload GetGenresByAuthorRequest request) {
        ParamsValidator.validateSearchParams(request.getSearch());
        long authorId = request.getAuthorId();
        if (!authorRepository.existsByAuthorId(authorId)) {
            throw new AuthorNotFoundException(authorId);
        }
        String sortOrder = request.getSearch().getOrderType();
        Sort sorter = Sort.by(Sort.Direction.fromString(sortOrder), DEFAULT_SORT_BY);

        List<Genre> rawGenres = genreRepository.getAllGenresOfAuthorOrdered(authorId, sorter);

        GetGenresByAuthorResponse response = new GetGenresByAuthorResponse();
        Genres responseGenres = new Genres();
        List<GenreType> mappedGenres = rawGenres.stream().map(this::mapGenreToGenreType).collect(Collectors.toList());
        responseGenres.getGenre().clear();
        responseGenres.getGenre().addAll(mappedGenres);
        response.setGenres(responseGenres);

        return response;
    }

    private GenreType mapGenreToGenreType(Genre genre) {
        GenreType genreType = new GenreType();
        genreType.setGenreId(genre.getGenreId());
        genreType.setGenreName(genre.getGenreName());
        genreType.setGenreDescription(genre.getDescription());
        return genreType;
    }

    private Genre mapGenreTypeToGenre(GenreType genreType) {
        Genre genre = new Genre();
        genre.setGenreId(genreType.getGenreId());
        genre.setGenreName(genreType.getGenreName());
        genre.setDescription(genreType.getGenreDescription());
        return genre;
    }
}
