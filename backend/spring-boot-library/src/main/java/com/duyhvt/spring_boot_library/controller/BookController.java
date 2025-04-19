package com.duyhvt.spring_boot_library.controller;

import com.duyhvt.spring_boot_library.entity.Book;
import com.duyhvt.spring_boot_library.service.BookService;
import com.duyhvt.spring_boot_library.utils.ExtractJWT;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/secure/current-loans/count")
    public int currentLoansCount(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "sub");
        return bookService.currentLoansCount(userEmail);
    }

    @GetMapping("/secure/ischeckedout/byuser")
    public Boolean isCheckedOutByUser(@RequestHeader(value = "Authorization") String token,
                                      @RequestParam Long bookId) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "sub");
        return bookService.checkoutBookByUser(userEmail, bookId);
    }

    @PutMapping("/secure/checkout")
    public Book checkoutBook(@RequestHeader(value = "Authorization") String token,
                             @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "sub");
        return bookService.checkoutBook(userEmail, bookId);
    }
}
