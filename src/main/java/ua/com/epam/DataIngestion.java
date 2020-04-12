package ua.com.epam;

import com.github.javafaker.Faker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

public class DataIngestion {
    private final static String fileName = "data.sql";
    private final static String fileLocation = "src/main/resources";

    private final static int authorsCount = 50; // 9999 - is maximum (if set to max it greatly increase generation time)
    private final static int genresCount = 15;   // 30 is maximum; if set more, will work endlessly!!!
    private final static int booksCount = 200;  // 9999 - is maximum (if set to max it greatly increase generation time)

    private static Faker f = new Faker();

    public static void main(String[] args) throws ParseException {
        List<String> bashLines = new ArrayList<>();

        //Author
        bashLines.add("insert into author(author_id, first_name, second_name, birth_city, birth_country, birth_date, author_descr, nationality) values");
        String authorValue = "(%d, '%s', '%s', '%s', '%s', '%s', '%s', '%s')";
        //generate unique Author ids;
        List<Long> authorIds = new ArrayList<>();
        while (authorIds.size() < authorsCount) {
            long id = f.number().numberBetween(1L, 9999L);
            if (!authorIds.contains(id)) authorIds.add(id);
        }

        String[] nationalities = {"Albanian", "American", "Australian", "Austrian", "Belgian", "British", "Bulgarian",
                "Canadian", "Chinese", "Czech", "Dutch", "Egyptian", "French", "German", "Greek", "Indian", "Irish",
                "Lithuanian", "Malaysian", "Mexican", "Moldovan", "New Zealander", "Romanian", "Scottish", "Spanish",
                "Swedish", "Turkish", "Ukrainian", "Welsh", "Syrian", "Slovenian", "Slovakian", "Polish", "Peruvian",
                "Namibian", "Nepalese", "Afghan", "Andorran", "Angolan", "Armenian", "Bahamian", "Cambodian",
                "Central African", "Colombian", "Cuban", "Equatorial Guinean", "Icelander", "Indonesian",
                "Kittian and Nevisian", "Liechtensteiner", "Lithuanian", "Luxembourger", "Maldivan", "Mongolian"};

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date from = formatter.parse("1920-01-01");
        Date to = formatter.parse("1999-12-31");

        bashLines.add(authorIds.stream().map(id -> String.format(authorValue,
                id,
                normalizeString(f.name().firstName()),
                normalizeString(f.name().lastName()),
                normalizeString(f.address().city()),
                normalizeString(f.address().country()),
                formatter.format(f.date().between(from, to)),
                normalizeString(f.lorem().paragraph()),
                normalizeString(nationalities[new Random().nextInt(nationalities.length)])))
                .reduce((a, b) -> a + ",\n" + b).get() + ";");

        //genre
        bashLines.add("\ninsert into genre(genre_id, genre_name, genre_descr) values");
        String genreValue = "(%d, '%s', '%s')";
        //generate all possible genre names
        List<String> genreNames = new ArrayList<>();
        while (genreNames.size() < genresCount) {
            String name = f.book().genre();
            if (!genreNames.contains(name)) genreNames.add(name);
        }

        //generate unique genre ids
        List<Long> genreIds = new ArrayList<>();
        while (genreIds.size() < genreNames.size()) {
            long id = f.number().numberBetween(1L, 9999L);
            if (!genreIds.contains(id)) genreIds.add(id);
        }

        bashLines.add(IntStream.range(0, genreIds.size())
                .mapToObj(i -> String.format(genreValue,
                        genreIds.get(i),
                        normalizeString(genreNames.get(i)),
                        normalizeString(f.lorem().paragraph())))
                .reduce((a, b) -> a + ",\n" + b).get() + ";");

        //book
        bashLines.add("\ninsert into book (book_id, book_name, book_language, book_descr, book_height, book_length, book_width, page_count, publication_year, author_id, genre_id) values");
        String bookValue = "(%d, '%s', '%s', '%s', %.1f, %.1f, %.1f, %d, %d, %d, %d)";

        List<Long> bookIds = new ArrayList<>();
        while (bookIds.size() < booksCount) {
            long id = f.number().numberBetween(1L, 9999L);
            if (!bookIds.contains(id)) bookIds.add(id);
        }

        String[] languages = {"ukrainian", "german", "russian", "polish", "spanish", "belorussian", "chinese", "english",
                "portuguese", "croatian", "french", "arabic", "armenian", "urdu", "farsi"};

        // doing Locale.US to print doubles with dot instead of comma
        bashLines.add(bookIds.stream().map(bookId -> String.format(Locale.US, bookValue,
                bookId,
                normalizeString(generateBookName()),
                normalizeString(languages[new Random().nextInt(languages.length)]),
                normalizeString(f.lorem().paragraph()),
                f.number().randomDouble(1, 5, 40),
                f.number().randomDouble(1, 1, 5),
                f.number().randomDouble(1, 5, 40),
                f.number().numberBetween(10, 1000),
                f.number().numberBetween(1970, 2019),
                authorIds.get(new Random().nextInt(authorIds.size())),
                genreIds.get(new Random().nextInt(genreIds.size()))))
                .reduce((a, b) -> a + ",\n" + b).get() + ";");

        File script = new File(fileLocation + "/" + fileName);
        try {
            script.createNewFile();
            Files.write(script.toPath(), bashLines);
            System.out.println("File " + fileName + " created in: " + fileLocation + "!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String normalizeString(String value) {
        return value.replace("'", "''");
    }

    private static String generateBookName() {
        List<String> words = new ArrayList<>();
        IntStream.rangeClosed(0, f.number().numberBetween(2, 5))
                .forEach(i -> words.add(f.lorem().word()));
        Collections.shuffle(words);
        String first = words.get(0);
        words.set(0, first.substring(0, 1).toUpperCase() + first.substring(1));
        return String.join(" ", words);
    }
}
