package com.campus.canteen.config;

import com.campus.canteen.entity.*;
import com.campus.canteen.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(AdminRepository adminRepo, UserRepository userRepo,
                                       CanteenRepository canteenRepo, CategoryRepository catRepo,
                                       DishRepository dishRepo, BCryptPasswordEncoder encoder) {
        return args -> {
            String rawPassword = "123456";
            String encodedPassword = encoder.encode(rawPassword);
            log.info("BCrypt hash for '{}' = {}", rawPassword, encodedPassword);

            // ===== 食堂 =====
            if (canteenRepo.count() == 0) {
                canteenRepo.save(createCanteen("第一食堂", "校园东区一楼", "王经理"));
                canteenRepo.save(createCanteen("第二食堂", "校园西区二楼", "赵经理"));
                canteenRepo.save(createCanteen("教工餐厅", "行政楼B1层", "陈经理"));
                canteenRepo.save(createCanteen("清真食堂", "校园东区二楼", "马经理"));
                log.info("Created 4 canteens");
            }

            // ===== 菜品分类 =====
            if (catRepo.count() == 0) {
                String[] catNames = {"特色大荤","精美小荤","时令蔬菜","主食面点","汤羹","水果甜点","川湘味","特色小吃","清真"};
                for (int i = 0; i < catNames.length; i++) {
                    Category c = new Category();
                    c.setName(catNames[i]);
                    c.setSortOrder(i + 1);
                    catRepo.save(c);
                }
                log.info("Created {} categories", catNames.length);
            }

            // ===== 菜品 =====
            if (dishRepo.count() == 0) {
                String[][] dishes = {
                    {"FD-2026-001","秘制红烧牛腩","1","1","3","12.00","450","高蛋白","85","1"},
                    {"FD-2026-002","宫保鸡丁套饭","2","1","3","10.00","380","咸甜适中","50","1"},
                    {"FD-2026-003","清蒸鲈鱼片","1","3","7","15.00","280","健康轻食","30","1"},
                    {"FD-2026-004","番茄炒蛋面","4","2","6","8.50","380","营养均衡","45","0"},
                    {"FD-2026-005","糖醋排骨","1","4","9","14.00","520","招牌必点","20","1"},
                    {"FD-2026-006","手撕包菜","3","1","2","4.50","120","清爽可口","60","0"},
                    {"FD-2026-007","银耳雪梨汤","5","1","4","3.50","80","润肺养颜","80","0"},
                    {"FD-2026-008","麻婆豆腐套饭","7","3","7","8.50","420","麻辣鲜香","35","0"},
                    {"FD-2026-009","鲜虾小馄饨","8","2","6","6.00","200","鲜美嫩滑","40","0"},
                };

                // 每道菜配对应的食物图片 Unsplash
                String[] images = {
                    "https://modao.cc/agent-py/media/generated_images/2026-04-30/0867b32ab91f46e6bebcc1e0774bd519.jpg",
                    "https://modao.cc/agent-py/media/generated_images/2026-04-30/252859d8eeb54530ae1ac9edfcb941a3.jpg",
                    "https://modao.cc/agent-py/media/generated_images/2026-04-30/8e50eb8d4716443684ccad60f9798a69.jpg",
                    "https://modao.cc/agent-py/media/generated_images/2026-04-30/0f60c624817749e199bfc275a3a3329b.jpg",
                    "https://modao.cc/agent-py/media/generated_images/2026-04-30/3821939504fd4fb98a412f322590733d.jpg",
                    "https://modao.cc/agent-py/media/generated_images/2026-04-30/35b9a2fda2634e3791981bb293bd4fd4.jpg",
                    "https://modao.cc/agent-py/media/generated_images/2026-04-30/bcbc220d37b74ff9bb04b08a84a7cf3d.jpg",
                    "https://modao.cc/agent-py/media/generated_images/2026-04-30/9210c0fca13041b895c14aaac42a3d08.jpg",
                    "https://modao.cc/agent-py/media/generated_images/2026-04-30/a62173a763ea4eba9c0861c91d34da2f.jpg",
                };

                for (int i = 0; i < dishes.length; i++) {
                    String[] d = dishes[i];
                    dishRepo.save(Dish.builder()
                            .dishNo(d[0])
                            .name(d[1])
                            .categoryId(Long.parseLong(d[2]))
                            .canteenId(Long.parseLong(d[3]))
                            .windowId(Long.parseLong(d[4]))
                            .price(new BigDecimal(d[5]))
                            .calories(Integer.parseInt(d[6]))
                            .nutritionTags(d[7])
                            .dailyStock(Integer.parseInt(d[8]))
                            .totalSold(0)
                            .image(images[i])
                            .isRecommended(Integer.parseInt(d[9]))
                            .status(Dish.Status.ON)
                            .build());
                }
                log.info("Created {} dishes with images", dishes.length);
            }

            // ===== 确保所有菜品都有真实食物图片 =====
            String[][] dishImageMap = {
                {"FD-2026-001", "https://modao.cc/agent-py/media/generated_images/2026-04-30/0867b32ab91f46e6bebcc1e0774bd519.jpg"},
                {"FD-2026-002", "https://modao.cc/agent-py/media/generated_images/2026-04-30/252859d8eeb54530ae1ac9edfcb941a3.jpg"},
                {"FD-2026-003", "https://modao.cc/agent-py/media/generated_images/2026-04-30/8e50eb8d4716443684ccad60f9798a69.jpg"},
                {"FD-2026-004", "https://modao.cc/agent-py/media/generated_images/2026-04-30/0f60c624817749e199bfc275a3a3329b.jpg"},
                {"FD-2026-005", "https://modao.cc/agent-py/media/generated_images/2026-04-30/3821939504fd4fb98a412f322590733d.jpg"},
                {"FD-2026-006", "https://modao.cc/agent-py/media/generated_images/2026-04-30/35b9a2fda2634e3791981bb293bd4fd4.jpg"},
                {"FD-2026-007", "https://modao.cc/agent-py/media/generated_images/2026-04-30/bcbc220d37b74ff9bb04b08a84a7cf3d.jpg"},
                {"FD-2026-008", "https://modao.cc/agent-py/media/generated_images/2026-04-30/9210c0fca13041b895c14aaac42a3d08.jpg"},
                {"FD-2026-009", "https://modao.cc/agent-py/media/generated_images/2026-04-30/a62173a763ea4eba9c0861c91d34da2f.jpg"},
            };
            for (String[] dm : dishImageMap) {
                dishRepo.findByDishNo(dm[0]).ifPresent(dish -> {
                    dish.setImage(dm[1]);
                    dishRepo.save(dish);
                    log.info("Set image for dish: {} -> real food photo", dish.getName());
                });
            }

            // ===== 管理员账号 =====
            if (adminRepo.findByUsername("admin_sa_01").isEmpty()) {
                adminRepo.save(Admin.builder()
                        .username("admin_sa_01").password(encodedPassword)
                        .name("超级管理员").role(Admin.Role.SUPER_ADMIN)
                        .status(Admin.Status.ACTIVE).build());
                log.info("Created admin: admin_sa_01");
            }
            if (adminRepo.findByUsername("admin_canteen_01").isEmpty()) {
                adminRepo.save(Admin.builder()
                        .username("admin_canteen_01").password(encodedPassword)
                        .name("第一食堂经理").role(Admin.Role.CANTEEN_MANAGER)
                        .canteenId(1L).status(Admin.Status.ACTIVE).build());
                log.info("Created admin: admin_canteen_01");
            }
            if (adminRepo.findByUsername("admin_supervisor_01").isEmpty()) {
                adminRepo.save(Admin.builder()
                        .username("admin_supervisor_01").password(encodedPassword)
                        .name("后勤处领导").role(Admin.Role.SUPERVISOR)
                        .status(Admin.Status.ACTIVE).build());
                log.info("Created admin: admin_supervisor_01");
            }

            // ===== 测试学生 =====
            if (userRepo.findByUserNo("20261021").isEmpty()) {
                userRepo.save(User.builder().userNo("20261021").name("张明伟")
                        .password(encodedPassword).role(User.Role.STUDENT)
                        .department("计算机学院").balance(new BigDecimal("286.50")).faceData(1).status(User.Status.NORMAL).build());
            }
            if (userRepo.findByUserNo("2026102401").isEmpty()) {
                userRepo.save(User.builder().userNo("2026102401").name("李华")
                        .password(encodedPassword).role(User.Role.STUDENT)
                        .department("艺术设计学院").balance(new BigDecimal("286.50")).status(User.Status.NORMAL).build());
            }
            if (userRepo.findByUserNo("20261034").isEmpty()) {
                userRepo.save(User.builder().userNo("20261034").name("李梦雨")
                        .password(encodedPassword).role(User.Role.STUDENT)
                        .department("外语学院").balance(new BigDecimal("520.00")).faceData(1).status(User.Status.NORMAL).build());
            }

            log.info("=== Test data ready ===");
            log.info("Admin: admin_sa_01 / {}", rawPassword);
            log.info("Student: 20261021 / {}", rawPassword);
        };
    }

    private Canteen createCanteen(String name, String location, String manager) {
        Canteen c = new Canteen();
        c.setName(name);
        c.setLocation(location);
        c.setManager(manager);
        c.setStatus(Canteen.Status.OPEN);
        return c;
    }
}
