package com.sivi.wallet.config;

import com.sivi.wallet.entity.Category;
import com.sivi.wallet.enums.CategoryType;
import com.sivi.wallet.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            List<Category> defaultCategories = List.of(
                    Category.builder().name("Ăn uống").type(CategoryType.EXPENSE).iconUrl("🍔").build(),
                    Category.builder().name("Di chuyển").type(CategoryType.EXPENSE).iconUrl("🛵").build(),
                    Category.builder().name("Đi chợ / Siêu thị").type(CategoryType.EXPENSE).iconUrl("🛒").build(),
                    Category.builder().name("Mua sắm").type(CategoryType.EXPENSE).iconUrl("🛍️").build(),
                    Category.builder().name("Giải trí").type(CategoryType.EXPENSE).iconUrl("🎬").build(),
                    Category.builder().name("Hóa đơn & Tiện ích").type(CategoryType.EXPENSE).iconUrl("💡").build(),
                    Category.builder().name("Sức khỏe").type(CategoryType.EXPENSE).iconUrl("🏥").build(),
                    Category.builder().name("Giáo dục").type(CategoryType.EXPENSE).iconUrl("📚").build(),
                    Category.builder().name("Lương & Thưởng").type(CategoryType.INCOME).iconUrl("💰").build(),
                    Category.builder().name("Thu nhập khác").type(CategoryType.INCOME).iconUrl("💵").build()
            );
            categoryRepository.saveAll(defaultCategories);
            System.out.println("✅ Successfully seeded 10 default system categories!");
        }
    }
}