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
            .should().bePackagePrivate()
            .andShould().resideInAnyPackage("..web..")
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
            .should().haveOnlyFinalFields();

    @ArchTest
    ArchRule representation = classes().that().areRecords()
            .should().resideInAnyPackage("..web..")
            .andShould().haveSimpleNameEndingWith("Representation");

    @ArchTest
    ArchRule layeredArchitecture = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Web").definedBy("..web..")
            .layer("Application").definedBy("..application..")
            .layer("Domain").definedBy("..domain..", "java.lang", "java.util..", "java.time")
            .layer("Persistence").definedBy("..persistence..")

            .whereLayer("Web").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Web")
            .whereLayer("Domain").mayNotAccessAnyLayer()
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Application");

}
