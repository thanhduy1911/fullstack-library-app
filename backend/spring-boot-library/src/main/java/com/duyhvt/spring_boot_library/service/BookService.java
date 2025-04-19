package com.duyhvt.spring_boot_library.service;

import com.duyhvt.spring_boot_library.dao.BookRepository;
import com.duyhvt.spring_boot_library.dao.CheckoutRepository;
import com.duyhvt.spring_boot_library.entity.Book;
import com.duyhvt.spring_boot_library.entity.Checkout;
import com.duyhvt.spring_boot_library.response_models.ShelfCurrentLoansResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final CheckoutRepository checkoutRepository;

    // Constructor Dependency Injection
    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository) {

        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
    }

    public Book checkoutBook(String userEmail, Long bookId) throws Exception {

        Optional<Book> book = bookRepository.findById(bookId);
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (book.isEmpty()) {
            throw new Exception("Book not found");
        }

        if (validateCheckout != null || book.get().getCopiesAvailable() <= 0) {
            throw new Exception("Book doesn't exist or already checkout out by this user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        bookRepository.save(book.get());

        // Start checkout transaction
        Checkout checkout = new Checkout(
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                book.get().getId()
        );
        checkoutRepository.save(checkout);

        return book.get();
    }

    public Boolean checkoutBookByUser(String userEmail, Long bookId) {

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        return validateCheckout != null;
    }

    public int currentLoansCount(String userEmail) {

        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {

        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses = new ArrayList<>();
        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        List<Long> bookIdList = new ArrayList<>();

        for (Checkout checkout : checkoutList) {
            bookIdList.add(checkout.getId());
        }

        List<Book> books = bookRepository.findBooksByBooksIds(bookIdList);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        for (Book book : books) {
            Optional<Checkout> checkout = checkoutList.stream()
                    .filter(checkout1 -> Objects.equals(checkout1.getBookId(), book.getId())).findFirst();

            if (checkout.isPresent()) {
                Date d1 = formatter.parse(checkout.get().getReturnDate());
                Date d2 = formatter.parse(LocalDate.now().toString());

                TimeUnit time = TimeUnit.DAYS;

                long diffInTime = time.convert(d1.getTime() - d2.getTime(),
                        TimeUnit.MILLISECONDS);

                shelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book, (int) diffInTime));
            }
        }
        return shelfCurrentLoansResponses;
    }
}
