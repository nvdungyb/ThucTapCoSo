package com.shopme.admin.product;

import com.shopme.admin.book.BookRepository;
import com.shopme.admin.clothes.ClothesRepository;
import com.shopme.admin.laptop.LaptopRepository;
import com.shopme.admin.shoe.ShoeRepository;
import com.shopme.common.entity.Seller;
import com.shopme.common.entity.User;
import com.shopme.common.shop.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepo;

    public static final int PRODUCTS_PER_PAGE = 8;
    @Autowired
    private LaptopRepository laptopRepository;
    @Autowired
    private ClothesRepository clothesRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ShoeRepository shoeRepository;

    public List<Product> listAll() {
        return (List<Product>) productRepo.findAll();
    }

    public Page<Product> listByPage(Integer pagenum, String keyword) {
        Pageable pageable = PageRequest.of(pagenum - 1, PRODUCTS_PER_PAGE);
        if (keyword != null && !keyword.isBlank()) {
            return productRepo.findAll(keyword, pageable);
        } else {
            return productRepo.findAll(pageable);
        }
    }

    public Product save(Product product, User user) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        } else {
            Product productInDB = productRepo.findById(product.getId()).get();
            product.setCreatedTime(productInDB.getCreatedTime());
        }

        if (product.getAlias() == null || product.getAlias().isBlank()) {
            String defaultAlias = product.getName().replace(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replace(" ", "-"));
        }

        product.setCreatedTime(new Date());
        product.setSeller((Seller) user);

        productRepo.save(product);
        return product;
    }

    public void updateProductEnabledStatus(Integer id, boolean enabled) {
        productRepo.updateEnabledStatus(id, enabled);
    }

    public void delete(Integer id) {
        productRepo.deleteById(id);
    }

    public Product findById(Integer id) {
        return productRepo.findById(id).get();
    }

    public Laptop saveLaptop(Laptop laptop, User user) {
        if (laptop.getId() == null) {
            laptop.setCreatedTime(new Date());
        } else {
            Laptop productInDB = laptopRepository.findById(laptop.getId()).get();
            laptop.setCreatedTime(productInDB.getCreatedTime());
        }

        if (laptop.getAlias() == null || laptop.getAlias().isBlank()) {
            String defaultAlias = laptop.getName().replace(" ", "-");
            laptop.setAlias(defaultAlias);
        } else {
            laptop.setAlias(laptop.getAlias().replace(" ", "-"));
        }

        laptop.setCreatedTime(new Date());
        laptop.setSeller((Seller) user);

        productRepo.save(laptop);
        return laptop;
    }

    public Clothes saveClothes(Clothes clothes, User user) {
        if (clothes.getId() == null) {
            clothes.setCreatedTime(new Date());
        } else {
            Clothes productInDB = clothesRepository.findById(clothes.getId()).get();
            clothes.setCreatedTime(productInDB.getCreatedTime());
        }

        if (clothes.getAlias() == null || clothes.getAlias().isBlank()) {
            String defaultAlias = clothes.getName().replace(" ", "-");
            clothes.setAlias(defaultAlias);
        } else {
            clothes.setAlias(clothes.getAlias().replace(" ", "-"));
        }

        clothes.setCreatedTime(new Date());
        clothes.setSeller((Seller) user);

        productRepo.save(clothes);
        return clothes;
    }

    public Book saveBook(Book book, User user) {
        if (book.getId() == null) {
            book.setCreatedTime(new Date());
        } else {
            Book productInDB = bookRepository.findById(book.getId()).get();
            book.setCreatedTime(productInDB.getCreatedTime());
        }

        if (book.getAlias() == null || book.getAlias().isBlank()) {
            String defaultAlias = book.getName().replace(" ", "-");
            book.setAlias(defaultAlias);
        } else {
            book.setAlias(book.getAlias().replace(" ", "-"));
        }

        book.setCreatedTime(new Date());
        book.setSeller((Seller) user);

        productRepo.save(book);
        return book;
    }

    public Shoe saveShoe(Shoe shoe, User user) {
        if (shoe.getId() == null) {
            shoe.setCreatedTime(new Date());
        } else {
            Shoe productInDB = shoeRepository.findById(shoe.getId()).get();
            shoe.setCreatedTime(productInDB.getCreatedTime());
        }

        if (shoe.getAlias() == null || shoe.getAlias().isBlank()) {
            String defaultAlias = shoe.getName().replace(" ", "-");
            shoe.setAlias(defaultAlias);
        } else {
            shoe.setAlias(shoe.getAlias().replace(" ", "-"));
        }

        shoe.setCreatedTime(new Date());
        shoe.setSeller((Seller) user);

        productRepo.save(shoe);
        return shoe;
    }
}
