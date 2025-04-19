package com.duyhvt.spring_boot_library.service;

import com.duyhvt.spring_boot_library.dao.BookRepository;
import com.duyhvt.spring_boot_library.dao.CheckoutRepository;
import com.duyhvt.spring_boot_library.entity.Book;
import com.duyhvt.spring_boot_library.entity.Checkout;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

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
}
