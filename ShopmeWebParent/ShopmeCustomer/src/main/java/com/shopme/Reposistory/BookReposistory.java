package com.shopme.Reposistory;

import com.shopme.common.shop.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BookReposistory extends CrudRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
}
