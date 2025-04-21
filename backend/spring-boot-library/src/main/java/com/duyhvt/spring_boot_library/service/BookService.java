package com.duyhvt.spring_boot_library.service;

import com.duyhvt.spring_boot_library.dao.BookRepository;
import com.duyhvt.spring_boot_library.dao.CheckoutRepository;
import com.duyhvt.spring_boot_library.dao.HistoryRepository;
import com.duyhvt.spring_boot_library.dao.PaymentRepository;
import com.duyhvt.spring_boot_library.entity.Book;
import com.duyhvt.spring_boot_library.entity.Checkout;
import com.duyhvt.spring_boot_library.entity.History;
import com.duyhvt.spring_boot_library.entity.Payment;
import com.duyhvt.spring_boot_library.response_models.ShelfCurrentLoansResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final CheckoutRepository checkoutRepository;
    private final HistoryRepository historyRepository;
    private final PaymentRepository paymentRepository;

    // Constructor Dependency Injection
    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository,
                       HistoryRepository historyRepository, PaymentRepository paymentRepository) {

        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.historyRepository = historyRepository;
        this.paymentRepository = paymentRepository;
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

        // return & force user to pay before checkout any new book
        List<Checkout> currentBooksCheckout = checkoutRepository.findBooksByUserEmail(userEmail);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        boolean bookNeedsReturned = false;
        for (Checkout checkout : currentBooksCheckout) {
            Date returnedDate = formatter.parse(checkout.getReturnDate());
            Date currentDate = formatter.parse(LocalDate.now().toString());

            TimeUnit timeUnit = TimeUnit.DAYS;
            double differenceInTime = timeUnit.convert(returnedDate.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);

            if (differenceInTime < 0) {
                bookNeedsReturned = true;
                break;
            }
        }

        Payment userPayment = paymentRepository.findByUserEmail(userEmail);
        if (userPayment != null && userPayment.getAmount() > 0 || (userPayment != null && bookNeedsReturned)) {
            throw new Exception("Outstanding fees");
        }

        if (userPayment == null) {
            Payment payment = new Payment();

            payment.setAmount(00.00);
            payment.setUserEmail(userEmail);

            paymentRepository.save(payment);
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
            bookIdList.add(checkout.getBookId());
        }

        List<Book> books = bookRepository.findBooksByBookIds(bookIdList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = sdf.parse(LocalDate.now().toString());
        TimeUnit time = TimeUnit.DAYS;
        for (Book book : books) {

            Optional<Checkout> checkout = checkoutList.stream().filter(x -> x.getBookId() == book.getId()).findFirst();
            if (checkout.isPresent()) {

                // Checkout should always be present
                Date returnDate = sdf.parse(checkout.get().getReturnDate());
                long differenceInTime = time.convert(returnDate.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);

                ShelfCurrentLoansResponse loanResponse = new ShelfCurrentLoansResponse(book, (int) differenceInTime);
                shelfCurrentLoansResponses.add(loanResponse);
            }
        }

        return shelfCurrentLoansResponses;
    }

    public void returnBook(String userEmail, Long bookId) throws Exception {

        Optional<Book> book = bookRepository.findById(bookId);
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateCheckout == null || book.isEmpty()) {
            throw new Exception("Book not found or not checkout by this user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);

        bookRepository.save(book.get());

        // Payment
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date returnedDate = sdf.parse(validateCheckout.getReturnDate());
        Date currentDate = sdf.parse(LocalDate.now().toString());

        TimeUnit timeUnit = TimeUnit.DAYS;

        double differenceInTime = timeUnit.convert(returnedDate.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);

        if (differenceInTime < 0) {
            Payment payment = paymentRepository.findByUserEmail(userEmail);
            if (payment != null) {
                payment.setAmount(payment.getAmount() + (differenceInTime * -1));

                paymentRepository.save(payment);
            }
        }
        checkoutRepository.deleteById(validateCheckout.getId());

        // Save return book to History
        History history = new History(
                userEmail,
                validateCheckout.getCheckoutDate(),
                LocalDate.now().toString(),
                book.get().getTitle(),
                book.get().getAuthor(),
                book.get().getDescription(),
                book.get().getImg()
        );

        historyRepository.save(history);
    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateCheckout == null) {
            throw new Exception("Book not found or not checkout by this user");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date returnDate = sdf.parse(validateCheckout.getReturnDate());
        Date currentDate = sdf.parse(LocalDate.now().toString());

        if (returnDate.compareTo(currentDate) >= 0) { // return date is greater than today date
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(validateCheckout);
        }
    }
}
