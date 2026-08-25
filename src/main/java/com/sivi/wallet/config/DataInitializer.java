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
        // 1. Seed Danh mục (giữ nguyên)
        if (categoryRepository.count() == 0) {
            categoryRepository.save(Category.builder().name("Ăn uống").type(CategoryType.EXPENSE).iconUrl("🍔").build());
            categoryRepository.save(Category.builder().name("Đi lại").type(CategoryType.EXPENSE).iconUrl("🛵").build());
        }

        // 2. TỰ ĐỘNG BƠM 3 USER + 1 VÍ 10 TRIỆU + 1 NHÓM TEST
        if (userRepository.count() == 0) {
            // Tạo 3 User: user1 (mình), user2 (Nam), user3 (Hùng) - Đều pass là 123456
            User u1 = userRepository.save(User.builder().username("user1").password(passwordEncoder.encode("123456")).fullName("Tech Lead Bri").isGuest(false).build());
            User u2 = userRepository.save(User.builder().username("user2").password(passwordEncoder.encode("123456")).fullName("Nam Cấp 3").isGuest(false).build());
            User u3 = userRepository.save(User.builder().username("user3").password(passwordEncoder.encode("123456")).fullName("Hùng Phòng Trọ").isGuest(false).build());

            // Bơm sẵn Ví Tiền Mặt 10.000.000đ cho user1
            walletRepository.save(Wallet.builder().userId(u1.getId()).name("Ví Tiền Mặt").walletType(WalletType.CASH).balance(new BigDecimal("10000000.00")).currency("VND").isActive(true).build());

            // Tạo sẵn Nhóm "Hội Ăn Lẩu 402" gồm cả 3 người
            Group g = groupRepository.save(Group.builder().name("Hội Ăn Lẩu 402").creatorId(u1.getId()).build());
            groupMemberRepository.save(GroupMember.builder().id(new GroupMemberId(g.getId(), u1.getId())).role(GroupRole.ADMIN).build());
            groupMemberRepository.save(GroupMember.builder().id(new GroupMemberId(g.getId(), u2.getId())).role(GroupRole.MEMBER).build());
            groupMemberRepository.save(GroupMember.builder().id(new GroupMemberId(g.getId(), u3.getId())).role(GroupRole.MEMBER).build());

            System.out.println("✅ Đã tự động tạo sẵn User1 (ID: 1), User2 (ID: 2), User3 (ID: 3), Ví 10tr (ID: 1), Nhóm (ID: 1)!");
        }
    }
}