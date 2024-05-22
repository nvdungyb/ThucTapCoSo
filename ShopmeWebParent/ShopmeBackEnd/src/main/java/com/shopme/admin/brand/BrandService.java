package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    public static final int BRANDS_PER_PAGE = 5;

    public List<Brand> findAll() {
        return (List<Brand>) brandRepository.findAll();
    }

    public Page<Brand> listByPage(Integer pageNum, String keyword) {
        Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE);
        if (keyword != null && !keyword.isEmpty()) {
            return brandRepository.findAll(keyword, pageable);
        } else {
            return brandRepository.findAll(pageable);
        }
    }

    public List<Brand> listAllBrandInForm() {
        return (List<Brand>) brandRepository.findAll();
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand get(Integer id) {
        return brandRepository.findById(id).get();
    }

    public void delete(Integer id) {
        brandRepository.deleteById(id);
    }

    public boolean checkUnique(Integer id, String name) {
        Brand brandByName = brandRepository.findByName(name);

        if (brandByName == null) return true;

        boolean isCreatingNew = (id == null || id == 0);

        if (isCreatingNew) {
            if (brandByName != null) return false;
        } else {
            if (brandByName.getId() != id) return false;
        }

        return true;
    }
}
