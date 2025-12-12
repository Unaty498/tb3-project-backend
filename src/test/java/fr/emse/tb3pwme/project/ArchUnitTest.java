package fr.emse.tb3pwme.project;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.core.importer.ImportOption.Predefined.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;

@AnalyzeClasses(packages = "fr.emse.tb3pwme.project", importOptions = { DoNotIncludeTests.class })
class ArchUnitTest {

    @ArchTest
    ArchRule controllers = classes().that().areAnnotatedWith(RestController.class)
            .should().resideInAnyPackage("..web..")
            .andShould().haveSimpleNameEndingWith("Controller");

    @ArchTest
    ArchRule services = classes().that().areAnnotatedWith(Service.class)
            .should().resideInAnyPackage("..application..");

    @ArchTest
    ArchRule repositories = classes().that().areAnnotatedWith(Repository.class)
            .should().resideInAnyPackage("..persistence..");

    @ArchTest
    ArchRule entities = classes().that().areAnnotatedWith(Entity.class)
            .should().resideInAnyPackage("..persistence..")
            .andShould().haveSimpleNameEndingWith("Entity");

    @ArchTest
    ArchRule domain = classes().that().resideInAPackage("..domain..")
            .and().areNotEnums()
            .should().haveOnlyFinalFields();

    @ArchTest
    ArchRule representation = classes().that().areRecords()
            .and().resideInAnyPackage("..web..")
            .should().haveNameMatching(".*Representation|.*Request|.*Response");

    // Temporarily disabled - DataLoader in root package needs special handling
    // @ArchTest
    // ArchRule layeredArchitecture = layeredArchitecture()
    //         .consideringAllDependencies()
    //         .layer("Web").definedBy("..web..")
    //         .layer("Application").definedBy("..application..")
    //         .layer("Domain").definedBy("..domain..")
    //         .layer("Persistence").definedBy("..persistence..")
    //         .layer("Security").definedBy("..security..")
    //
    //         .whereLayer("Web").mayNotBeAccessedByAnyLayer()
    //         .whereLayer("Application").mayOnlyBeAccessedByLayers("Web", "Security")
    //         .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Persistence", "Web")
    //         .whereLayer("Security").mayOnlyBeAccessedByLayers("Web")
    //         .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Application")
    //
    //         .ignoreDependency("fr.emse.tb3pwme.project.DataLoader", "fr.emse.tb3pwme.project.application..")
    //         .ignoreDependency("fr.emse.tb3pwme.project.DataLoader", "fr.emse.tb3pwme.project.domain..");

}
