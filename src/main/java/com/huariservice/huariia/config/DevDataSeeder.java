package com.huariservice.huariia.config;

import com.huariservice.huariia.entities.*;
import com.huariservice.huariia.entities.enums.ParliamentarianChamber;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@Profile("dev")
@Order(2)
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;
    private final ParliamentarianRepository parliamentarianRepository;
    private final ParliamentarianExpenseRepository expenseRepository;
    private final LegislativeBillRepository legislativeBillRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("[DevDataSeeder] Verificando dados iniciais para ambiente de desenvolvimento...");

        // 1. Usuários de Teste (Aluno e Administrador)
        if (!userRepository.existsByEmail("aluno@huari.com")) {
            User aluno = new User();
            aluno.setName("Aluno Cidadão");
            aluno.setEmail("aluno@huari.com");
            aluno.setPasswordHash(passwordEncoder.encode("aluno123456"));
            aluno.setRole(Role.STUDENT);
            userRepository.save(aluno);
            log.info("[DevDataSeeder] Usuário aluno criado: aluno@huari.com / aluno123456");
        }

        if (!userRepository.existsByEmail("admin@huari.com")) {
            User admin = new User();
            admin.setName("Administrador Huari");
            admin.setEmail("admin@huari.com");
            admin.setPasswordHash(passwordEncoder.encode("senha123456"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            log.info("[DevDataSeeder] Usuário admin criado: admin@huari.com / senha123456");
        }

        // 2. Cursos, Módulos e Aulas Cívicas
        if (courseRepository.count() == 0) {
            seedCivicCourses();
        }

        // 3. Parlamentares, Despesas CEAP e Projetos de Lei
        if (parliamentarianRepository.count() == 0) {
            seedTransparencyData();
        }

        log.info("[DevDataSeeder] Inicialização de dados de desenvolvimento concluída com sucesso!");
    }

    private void seedCivicCourses() {
        log.info("[DevDataSeeder] Populando catálogo de cursos e trilhas cívicas...");

        Category catIniciantes = categoryRepository.save(new Category(null, "Beginners", "Fundamentos da cidadania e participação democrática"));
        Category catLegislativo = categoryRepository.save(new Category(null, "Legislative", "Tramitação de leis, comissões e funcionamento do Congresso"));
        Category catOrcamento = categoryRepository.save(new Category(null, "Budget & Auditing", "Auditoria de gastos, transparência fiscal e cota parlamentar"));
        Category catAcaoCivica = categoryRepository.save(new Category(null, "Civic Action", "Estratégias práticas de controle social e engajamento"));

        Author autorHuari = authorRepository.save(new Author(null, "Huari Civic Academy", "https://youtube.com/@huariacademy"));
        Author autorEscola = authorRepository.save(new Author(null, "Escola de Cidadania", "https://youtube.com/@escolacidadania"));

        // Curso 1: Introdução ao Processo Legislativo
        Course curso1 = courseRepository.save(new Course(
                null,
                "Anatomia do Processo Legislativo Brasileiro",
                "Entenda como nascem, tramitam e são votadas as leis no Brasil, desde o projeto de lei até a promulgação.",
                "https://www.youtube.com/watch?v=intro-legislativo",
                catLegislativo,
                autorHuari
        ));

        CourseModule mod1 = courseModuleRepository.save(new CourseModule(
                null,
                "Módulo 1: O Caminho das Leis",
                "Conceito de PL, PLP, PEC e Medida Provisória.",
                1,
                curso1
        ));

        lessonRepository.save(new Lesson(
                null,
                "Aula 1: A diferença entre PL, PEC e MP",
                "Entenda os quóruns de votação e o rito de tramitação na Câmara e no Senado.",
                "https://www.youtube.com/watch?v=pl-pec-mp",
                1,
                15,
                mod1
        ));

        lessonRepository.save(new Lesson(
                null,
                "Aula 2: As Comissões Temáticas (CCJ e outras)",
                "O papel das comissões na admissibilidade jurídica e discussão de mérito.",
                "https://www.youtube.com/watch?v=comissoes-tematicas",
                2,
                18,
                mod1
        ));

        CourseModule mod2 = courseModuleRepository.save(new CourseModule(
                null,
                "Módulo 2: Votações em Plenário e Sanção",
                "O papel do Presidente da Câmara, os turnos e o veto presidencial.",
                2,
                curso1
        ));

        lessonRepository.save(new Lesson(
                null,
                "Aula 3: Votação Nominal vs Votação Simbólica",
                "Como auditar a presença e o posicionamento de cada deputado no painel.",
                "https://www.youtube.com/watch?v=votacao-plenario",
                1,
                14,
                mod2
        ));

        // Curso 2: Auditoria de Cota Parlamentar (CEAP)
        Course curso2 = courseRepository.save(new Course(
                null,
                "Auditoria Cidadã de Cota Parlamentar (CEAP)",
                "Aprenda a fiscalizar os reembolsos de despesas de deputados federais e senadores usando dados abertos e notas fiscais.",
                "https://www.youtube.com/watch?v=auditoria-ceap",
                catOrcamento,
                autorHuari
        ));

        CourseModule modCeap1 = courseModuleRepository.save(new CourseModule(
                null,
                "Módulo 1: Entendendo a Cota CEAP",
                "Limites por estado, regras de elegibilidade e itens vedados.",
                1,
                curso2
        ));

        lessonRepository.save(new Lesson(
                null,
                "Aula 1: O que a cota pode e não pode cobrir",
                "Passagens, consultorias, combustíveis e regras do ato da mesa diretora.",
                "https://www.youtube.com/watch?v=regras-ceap",
                1,
                16,
                modCeap1
        ));

        lessonRepository.save(new Lesson(
                null,
                "Aula 2: Rastreando CNPJs e Notas Fiscais Eletrônicas",
                "Passo a passo no Portal de Dados Abertos e na Receita Federal.",
                "https://www.youtube.com/watch?v=consultar-notas",
                2,
                20,
                modCeap1
        ));

        // Curso 3: Orçamento Municipal
        Course curso3 = courseRepository.save(new Course(
                null,
                "Orçamento Municipal e Fiscalização Cidadã",
                "Como funciona o PPA, a LDO e a LOA nos municípios brasileiros e como fiscalizar a aplicação dos recursos públicos.",
                "https://www.youtube.com/watch?v=orcamento-municipal",
                catIniciantes,
                autorEscola
        ));

        CourseModule modMun1 = courseModuleRepository.save(new CourseModule(
                null,
                "Módulo 1: O Ciclo Orçamentário",
                "Planejamento, diretrizes e execução orçamentária.",
                1,
                curso3
        ));

        lessonRepository.save(new Lesson(
                null,
                "Aula 1: Como ler a Lei Orçamentária Anual (LOA)",
                "Fontes de receita, despesas obrigatórias e discricionárias.",
                "https://www.youtube.com/watch?v=ciclo-loa",
                1,
                15,
                modMun1
        ));

        log.info("[DevDataSeeder] 3 cursos cívicos cadastrados com módulos e aulas.");
    }

    private void seedTransparencyData() {
        log.info("[DevDataSeeder] Populando parlamentares, despesas e projetos de lei...");

        // Parlamentares
        Parliamentarian tabata = parliamentarianRepository.save(new Parliamentarian(
                null, "Tabata Amaral", ParliamentarianChamber.CHAMBER_OF_DEPUTIES, "PSB", "SP", "204536"));

        Parliamentarian nikolas = parliamentarianRepository.save(new Parliamentarian(
                null, "Nikolas Ferreira", ParliamentarianChamber.CHAMBER_OF_DEPUTIES, "PL", "MG", "204526"));

        Parliamentarian erika = parliamentarianRepository.save(new Parliamentarian(
                null, "Erika Hilton", ParliamentarianChamber.CHAMBER_OF_DEPUTIES, "PSOL", "SP", "220639"));

        Parliamentarian kim = parliamentarianRepository.save(new Parliamentarian(
                null, "Kim Kataguiri", ParliamentarianChamber.CHAMBER_OF_DEPUTIES, "UNIÃO", "SP", "204536"));

        Parliamentarian pacheco = parliamentarianRepository.save(new Parliamentarian(
                null, "Rodrigo Pacheco", ParliamentarianChamber.FEDERAL_SENATE, "PSD", "MG", "5012"));

        Parliamentarian randolfe = parliamentarianRepository.save(new Parliamentarian(
                null, "Randolfe Rodrigues", ParliamentarianChamber.FEDERAL_SENATE, "PT", "AP", "5011"));

        // Despesas CEAP para os parlamentares
        seedExpensesFor(tabata, 12400.0, 8900.5, 4500.0, 6200.0);
        seedExpensesFor(nikolas, 15600.0, 9400.0, 5200.0, 7100.0);
        seedExpensesFor(erika, 11200.0, 7800.0, 3900.0, 5500.0);
        seedExpensesFor(kim, 13100.0, 8100.0, 4100.0, 6800.0);
        seedExpensesFor(pacheco, 18500.0, 11200.0, 6400.0, 8900.0);
        seedExpensesFor(randolfe, 14200.0, 9100.0, 4800.0, 7200.0);

        // Projetos de Lei para RAG
        legislativeBillRepository.save(new LegislativeBill(
                null,
                "PL 2338/2023",
                "Marco Legal da Inteligência Artificial",
                "Dispõe sobre o uso da inteligência artificial no Brasil, estabelecendo princípios, direitos civis dos cidadãos, classificação de riscos e mecanismos de governança e transparência algorítmica.",
                "Art. 1º Esta Lei estabelece normas gerais para o desenvolvimento, implementação e uso de sistemas de inteligência artificial (IA) no Brasil...",
                "EM TRAMITAÇÃO",
                pacheco
        ));

        legislativeBillRepository.save(new LegislativeBill(
                null,
                "PEC 45/2019",
                "Reforma Tributária sobre o Consumo",
                "Altera o Sistema Tributário Nacional para extinguir tributos cumulativos e instituir o Imposto sobre Bens e Serviços (IBS) e a Contribuição sobre Bens e Serviços (CBS).",
                "Art. 1º A Constituição Federal passa a vigorar com as seguintes alterações referentes à unificação e transparência tributária...",
                "PROMULGADA",
                tabata
        ));

        legislativeBillRepository.save(new LegislativeBill(
                null,
                "PL 2630/2020",
                "Lei Brasileira de Liberdade, Responsabilidade e Transparência na Internet",
                "Institui a Lei das Fake News estabelecendo dever de cuidado, relatórios de transparência para grandes plataformas digitais e salvaguardas da liberdade de expressão.",
                "Art. 1º Esta Lei institui a Lei Brasileira de Liberdade, Responsabilidade e Transparência na Internet...",
                "PRONTO PARA PAUTA",
                randolfe
        ));

        log.info("[DevDataSeeder] 6 parlamentares, despesas e 3 PLs principais cadastrados com sucesso.");
    }

    private void seedExpensesFor(Parliamentarian p, double divulgacao, double passagens, double veiculos, double consultorias) {
        LocalDate now = LocalDate.now();
        expenseRepository.save(new ParliamentarianExpense(
                null, p, "Divulgação da Atividade Parlamentar", "Gráfica & Comunicação Digital Brasil",
                BigDecimal.valueOf(divulgacao), now.minusDays(5), "https://camara.leg.br/doc-exemplo-1.pdf"));

        expenseRepository.save(new ParliamentarianExpense(
                null, p, "Passagens Aéreas e Hospedagem", "Companhias Aéreas Nacionais",
                BigDecimal.valueOf(passagens), now.minusDays(12), "https://camara.leg.br/doc-exemplo-2.pdf"));

        expenseRepository.save(new ParliamentarianExpense(
                null, p, "Locação de Veículos e Combustíveis", "Locadora de Veículos Brasília",
                BigDecimal.valueOf(veiculos), now.minusDays(18), "https://camara.leg.br/doc-exemplo-3.pdf"));

        expenseRepository.save(new ParliamentarianExpense(
                null, p, "Consultorias e Pesquisas Técnicas", "Instituto de Políticas Públicas Aplicadas",
                BigDecimal.valueOf(consultorias), now.minusDays(25), "https://camara.leg.br/doc-exemplo-4.pdf"));
    }
}
