package com.sivi.wallet.config;

import com.sivi.wallet.entity.*;
import com.sivi.wallet.enums.*;
import com.sivi.wallet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Seed 10 Default System Categories (userId = null)
        if (categoryRepository.count() == 0) {
            List<Category> defaultCategories = List.of(
                    Category.builder().name("Ăn uống").type(CategoryType.EXPENSE).iconUrl("🍔").isActive(true).build(),
                    Category.builder().name("Di chuyển").type(CategoryType.EXPENSE).iconUrl("🛵").isActive(true).build(),
                    Category.builder().name("Đi chợ / Siêu thị").type(CategoryType.EXPENSE).iconUrl("🛒").isActive(true).build(),
                    Category.builder().name("Mua sắm").type(CategoryType.EXPENSE).iconUrl("🛍️").isActive(true).build(),
                    Category.builder().name("Giải trí").type(CategoryType.EXPENSE).iconUrl("🎬").isActive(true).build(),
                    Category.builder().name("Hóa đơn & Tiện ích").type(CategoryType.EXPENSE).iconUrl("💡").isActive(true).build(),
                    Category.builder().name("Sức khỏe").type(CategoryType.EXPENSE).iconUrl("🏥").isActive(true).build(),
                    Category.builder().name("Giáo dục").type(CategoryType.EXPENSE).iconUrl("📚").isActive(true).build(),
                    Category.builder().name("Lương & Thưởng").type(CategoryType.INCOME).iconUrl("💰").isActive(true).build(),
                    Category.builder().name("Thu nhập khác").type(CategoryType.INCOME).iconUrl("💵").isActive(true).build()
            );
            categoryRepository.saveAll(defaultCategories);
            System.out.println("✅ Seeded 10 default system categories successfully!");
        }

        // 2. Seed Mock Users, Wallets, and Group
        if (userRepository.count() == 0) {
            // Seed registered users (password: 123456)
            User u1 = userRepository.save(User.builder().username("user1").email("user1@sivi.vn").password(passwordEncoder.encode("123456")).fullName("Tech Lead Bri").isGuest(false).build());
            User u2 = userRepository.save(User.builder().username("user2").email("user2@sivi.vn").password(passwordEncoder.encode("123456")).fullName("Nam Cấp 3").isGuest(false).build());
            User u3 = userRepository.save(User.builder().username("user3").email("user3@sivi.vn").password(passwordEncoder.encode("123456")).fullName("Hùng Phòng Trọ").isGuest(false).build());

            // Seed guest user
            User guest1 = userRepository.save(User.builder().fullName("Bé Lan (Khách)").isGuest(true).build());

            // Seed wallets for user1
            walletRepository.save(Wallet.builder().userId(u1.getId()).name("Ví Tiền Mặt").walletType(WalletType.CASH).balance(new BigDecimal("10000000.00")).currency("VND").isActive(true).build());
            walletRepository.save(Wallet.builder().userId(u1.getId()).name("Ví MoMo").walletType(WalletType.E_WALLET).balance(new BigDecimal("5000000.00")).currency("VND").isActive(true).build());

            // Seed wallets for user2 & user3
            walletRepository.save(Wallet.builder().userId(u2.getId()).name("Ví Tiền Mặt").walletType(WalletType.CASH).balance(new BigDecimal("3000000.00")).currency("VND").isActive(true).build());
            walletRepository.save(Wallet.builder().userId(u3.getId()).name("Ví Tiền Mặt").walletType(WalletType.CASH).balance(new BigDecimal("1500000.00")).currency("VND").isActive(true).build());

            // Seed Group "Hội Ăn Lẩu 402"
            Group group = groupRepository.save(Group.builder().name("Hội Ăn Lẩu 402").creatorId(u1.getId()).build());
            groupMemberRepository.save(GroupMember.builder().id(new GroupMemberId(group.getId(), u1.getId())).role(GroupRole.ADMIN).build());
            groupMemberRepository.save(GroupMember.builder().id(new GroupMemberId(group.getId(), u2.getId())).role(GroupRole.MEMBER).build());
            groupMemberRepository.save(GroupMember.builder().id(new GroupMemberId(group.getId(), u3.getId())).role(GroupRole.MEMBER).build());
            groupMemberRepository.save(GroupMember.builder().id(new GroupMemberId(group.getId(), guest1.getId())).role(GroupRole.MEMBER).build());

            System.out.println("✅ Seeded mock users, wallets, and groups successfully!");
        }
    }
}