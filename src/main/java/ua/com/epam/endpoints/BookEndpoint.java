package ua.com.epam.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ua.com.epam.entities.*;
import ua.com.epam.entity.Book;
import ua.com.epam.exception.author.AuthorNotFoundException;
import ua.com.epam.exception.book.BookAlreadyExistsException;
import ua.com.epam.exception.book.BookNotFoundException;
import ua.com.epam.exception.genre.GenreNotFoundException;
import ua.com.epam.repository.AuthorRepository;
import ua.com.epam.repository.BookRepository;
import ua.com.epam.repository.GenreRepository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ua.com.epam.WebServiceConfig.WEB_SERVICE_NAMESPACE;

@Endpoint
public class BookEndpoint {

    private static final String NAMESPACE_URI = WEB_SERVICE_NAMESPACE;
    private static final String DEFAULT_SORT_BY = "bookId";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBookRequest")
    @ResponsePayload
    public GetBookResponse getBook(@RequestPayload GetBookRequest request) {
        long bookId = request.getBookId();
        Book book = bookRepository.getOneByBookId(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        GetBookResponse response = new GetBookResponse();
        response.setBook(mapBookToBookType(book));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBooksRequest")
    @ResponsePayload
    public GetBooksResponse getBooks(@RequestPayload GetBooksRequest request) {
        ParamsValidator.validateSearchParams(request.getSearch());
        Sort.Direction direction = Sort.Direction.fromString(request.getSearch().getOrderType());
        Sort sorter = Sort.by(direction, DEFAULT_SORT_BY);

        List<Book> rawBooks;

        if (!request.getSearch().isPagination()) {
            rawBooks = bookRepository.findAll(sorter);
        } else {
            rawBooks = bookRepository.getAllBooks(PageRequest.of(request.getSearch().getPage().intValue() - 1, request.getSearch().getSize(), sorter));
        }

        GetBooksResponse response = new GetBooksResponse();
        Books responseBooks = new Books();
        responseBooks.getBook().clear();
        responseBooks.getBook().addAll(rawBooks.stream().map(this::mapBookToBookType).collect(Collectors.toList()));
        response.setBooks(responseBooks);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createBookRequest")
    @ResponsePayload
    public CreateBookResponse createBook(@RequestPayload CreateBookRequest request) {
        long authorId = request.getAuthorId();
        long genreId = request.getGenreId();
        Book createdBook = mapBookTypeToBook(request.getBook());

        if (!authorRepository.existsByAuthorId(authorId)) {
            throw new AuthorNotFoundException(authorId);
        }

        if (!genreRepository.existsByGenreId(genreId)) {
            throw new GenreNotFoundException(genreId);
        }

        if (bookRepository.existsByBookId(createdBook.getBookId())) {
            throw new BookAlreadyExistsException(createdBook.getBookId());
        }

        createdBook.setAuthorId(authorId);
        createdBook.setGenreId(genreId);

        Book savedBook = bookRepository.save(createdBook);

        CreateBookResponse response = new CreateBookResponse();
        response.setBook(mapBookToBookType(savedBook));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateBookRequest")
    @ResponsePayload
    public UpdateBookResponse updateBook(@RequestPayload UpdateBookRequest request) {
        long bookId = request.getBook().getBookId();
        Book proxy = bookRepository.getOneByBookId(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        BookType newBook = request.getBook();

        proxy.setBookName(newBook.getBookName());
        proxy.setBookLang(newBook.getBookLanguage());
        proxy.setDescription(newBook.getBookDescription());
        proxy.setPublicationYear(newBook.getPublicationYear().intValue());
        proxy.setPageCount(newBook.getAdditional().getPageCount().intValue());
        proxy.setBookWidth(Double.valueOf(newBook.getAdditional().getSize().getWidth()));
        proxy.setBookLength(Double.valueOf(newBook.getAdditional().getSize().getLength()));
        proxy.setBookHeight(Double.valueOf(newBook.getAdditional().getSize().getHeight()));

        Book updatedBook = bookRepository.save(proxy);

        UpdateBookResponse response = new UpdateBookResponse();
        response.setBook(mapBookToBookType(updatedBook));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteBookRequest")
    @ResponsePayload
    public DeleteBookResponse deleteBook(@RequestPayload DeleteBookRequest request) {
        long bookId = request.getBookId();
        Book toDelete = bookRepository.getOneByBookId(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        bookRepository.delete(toDelete);

        DeleteBookResponse response = new DeleteBookResponse();
        response.setStatus("Successfully deleted book " + bookId);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchBooksRequest")
    @ResponsePayload
    public SearchBooksResponse searchBooks(@RequestPayload SearchBooksRequest request) {
        ParamsValidator.validateSearchParams(request.getSearch());
        String sortOrder = request.getSearch().getOrderType();
        int size = request.getSearch().getSize();
        String query = request.getQuery();

        String wordRegex = "\\S+";

        Pattern wordPattern = Pattern.compile(wordRegex);
        Matcher wordMatcher = wordPattern.matcher(query);
        List<String> queryWords = new ArrayList<>();
        while(wordMatcher.find()) {
            queryWords.add(wordMatcher.group().toLowerCase());
        }

        Sort sorter = Sort.by(Sort.Direction.fromString(sortOrder), DEFAULT_SORT_BY);
        List<Book> rawBooks = bookRepository.findAll(sorter).stream().filter(book -> {
            String bookName = book.getBookName();
            Matcher bookWordMatcher = wordPattern.matcher(bookName);
            List<String> bookWords = new ArrayList<>();
            while(bookWordMatcher.find()) {
                bookWords.add(bookWordMatcher.group());
            }
            // basically looking for a book where name contains every word from query (with startsWith logic)
            return queryWords.stream().allMatch(q -> bookWords.stream().anyMatch(bw -> bw.toLowerCase().startsWith(q)));
        }).limit(size).collect(Collectors.toList());

        SearchBooksResponse response = new SearchBooksResponse();
        Books responseBooks = new Books();
        responseBooks.getBook().clear();
        responseBooks.getBook().addAll(rawBooks.stream().map(this::mapBookToBookType).collect(Collectors.toList()));
        response.setBooks(responseBooks);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBooksByGenreRequest")
    @ResponsePayload
    public GetBooksByGenreResponse getBooksByGenre(@RequestPayload GetBooksByGenreRequest request) {
        ParamsValidator.validateSearchParams(request.getSearch());
        long genreId = request.getGenreId();
        if (!genreRepository.existsByGenreId(genreId)) {
            throw new GenreNotFoundException(genreId);
        }

        int size = request.getSearch().getSize();
        Sort sorter = Sort.by(Sort.Direction.fromString(request.getSearch().getOrderType()), DEFAULT_SORT_BY);

        List<Book> rawBooks;

        if (!request.getSearch().isPagination()) {
            rawBooks = bookRepository.getAllBooksInGenre(genreId, sorter);
        } else {
            rawBooks = bookRepository.getAllBooksInGenre(genreId, PageRequest.of(request.getSearch().getPage().intValue() - 1, size, sorter));
        }

        GetBooksByGenreResponse response = new GetBooksByGenreResponse();
        Books responseBooks = new Books();
        responseBooks.getBook().clear();
        responseBooks.getBook().addAll(rawBooks.stream().map(this::mapBookToBookType).collect(Collectors.toList()));
        response.setBooks(responseBooks);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBooksByAuthorRequest")
    @ResponsePayload
    public GetBooksByAuthorResponse getBooksByAuthor(@RequestPayload GetBooksByAuthorRequest request) {
        ParamsValidator.validateSearchParams(request.getSearch());
        long authorId = request.getAuthorId();
        if (!authorRepository.existsByAuthorId(authorId)) {
            throw new AuthorNotFoundException(authorId);
        }

        Sort sorter = Sort.by(Sort.Direction.fromString(request.getSearch().getOrderType()), DEFAULT_SORT_BY);

        List<Book> rawBooks = bookRepository.getAllAuthorBooksOrdered(authorId, sorter);

        GetBooksByAuthorResponse response = new GetBooksByAuthorResponse();
        Books responseBooks = new Books();
        responseBooks.getBook().clear();
        responseBooks.getBook().addAll(rawBooks.stream().map(this::mapBookToBookType).collect(Collectors.toList()));
        response.setBooks(responseBooks);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBooksByAuthorAndGenreRequest")
    @ResponsePayload
    public GetBooksByAuthorAndGenreResponse getBooksByAuthorAndGenre(@RequestPayload GetBooksByAuthorAndGenreRequest request) {
        long authorId = request.getAuthorId();
        if (!authorRepository.existsByAuthorId(authorId)) {
            throw new AuthorNotFoundException(authorId);
        }
        long genreId = request.getGenreId();
        if (!genreRepository.existsByGenreId(genreId)) {
            throw new GenreNotFoundException(genreId);
        }

        List<Book> rawBooks = bookRepository.getAllAuthorBooksInGenre(authorId, genreId);

        GetBooksByAuthorAndGenreResponse response = new GetBooksByAuthorAndGenreResponse();
        Books responseBooks = new Books();
        responseBooks.getBook().clear();
        responseBooks.getBook().addAll(rawBooks.stream().map(this::mapBookToBookType).collect(Collectors.toList()));
        response.setBooks(responseBooks);
        return response;
    }

    private BookType mapBookToBookType(Book book) {
        BookType bookType = new BookType();
        bookType.setBookId(book.getBookId());
        bookType.setBookName(book.getBookName());
        bookType.setBookDescription(book.getDescription());
        bookType.setBookLanguage(book.getBookLang());
        bookType.setPublicationYear(BigInteger.valueOf(book.getPublicationYear()));
        BookType.Additional additional = new BookType.Additional();
        additional.setPageCount(BigInteger.valueOf(book.getPageCount()));
        BookType.Additional.Size size = new BookType.Additional.Size();
        size.setHeight(book.getBookHeight().floatValue());
        size.setLength(book.getBookLength().floatValue());
        size.setWidth(book.getBookWidth().floatValue());
        additional.setSize(size);
        bookType.setAdditional(additional);
        return bookType;
    }

    private Book mapBookTypeToBook(BookType bookType) {
        Book book = new Book();
        book.setBookId(bookType.getBookId());
        book.setBookName(bookType.getBookName());
        book.setDescription(bookType.getBookDescription());
        book.setBookLang(bookType.getBookLanguage());
        book.setPublicationYear(bookType.getPublicationYear().intValue());
        book.setPageCount(bookType.getAdditional().getPageCount().intValue());
        book.setBookHeight(Double.valueOf(bookType.getAdditional().getSize().getHeight()));
        book.setBookLength(Double.valueOf(bookType.getAdditional().getSize().getLength()));
        book.setBookWidth(Double.valueOf(bookType.getAdditional().getSize().getWidth()));
        return book;
    }
}
