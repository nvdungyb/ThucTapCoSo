package com.shopme.admin.seller;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.Seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class SellerService {
    public static final int USERS_PER_PAGE = 5;

    @Autowired
    private SellerRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Seller> listAll() {
        return (List<Seller>) userRepo.findAll();
    }

    public Page<Seller> listByPage(int pageNum, String keyword) {
        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE);
        if (keyword != null) {
            return userRepo.findAll(keyword, pageable);
        }
        return userRepo.findAll(pageable);
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepo.findAll();
    }

    private void encodePassword(Seller user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public Seller save(Seller seller) {
        boolean isUpdatingUser = (seller.getId() != null);
        if (isUpdatingUser) {
            Seller exitingUser = userRepo.findById(seller.getId()).get();
            if (seller.getPassword().isEmpty()) {
                seller.setPassword(exitingUser.getPassword());
            } else {
                encodePassword(seller);
            }
        } else {
            encodePassword(seller);
        }
        return userRepo.save(seller);
    }

    public boolean isEmailUnique(Integer id, String email) {
        Seller userByEmail = userRepo.getUserByEmail(email);

        if (userByEmail == null)
            return true;

        boolean isCreatingNew = (id == null);

        if (isCreatingNew) {
            if (userByEmail != null) return false;
        } else {
            if (userByEmail.getId() != id) return false;
        }

        return true;
    }

    public Seller get(Integer id) throws SellerNotFoundException {
        try {
            return userRepo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new SellerNotFoundException("Could not find any user with ID " + id);
        }
    }

    public void delete(Integer id) throws SellerNotFoundException {
        Long countById = userRepo.countById(id);
        if (countById == null || countById == 0) {
            throw new SellerNotFoundException("Could not find any user with ID " + id);
        } else {
            userRepo.deleteById(id);
        }
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepo.updateEnabledStatus(id, enabled);
    }

    public void register(Seller seller) {
        encodePassword(seller);
        seller.setEnabled(true);
        userRepo.save(seller);
    }

    public void registerSeller(Seller seller) {
        seller.setNumberOfOrders(0);
        seller.setShopRating(0.0);
        seller.setShopName("shopname");
        seller.setTaxId("123243345");

        encodePassword(seller);
        seller.setEnabled(true);
        Optional<Role> roleSeller = roleRepo.findByName("SELLER");
        seller.addRole(roleSeller.get());
        userRepo.save(seller);
    }
}
