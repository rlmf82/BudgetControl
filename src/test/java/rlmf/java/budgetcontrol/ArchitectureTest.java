package rlmf.java.budgetcontrol;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "rlmf.java.budgetcontrol", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_must_stay_framework_free =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta.persistence..");

    @ArchTest
    static final ArchRule domain_must_not_depend_on_adapters =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter..");

    @ArchTest
    static final ArchRule application_must_not_depend_on_adapters =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter..");

    @ArchTest
    static final ArchRule web_adapter_must_not_depend_on_persistence_adapter =
            noClasses().that().resideInAPackage("..adapter.in.web..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter.out.persistence..");

    @ArchTest
    static final ArchRule persistence_adapter_must_not_depend_on_web_adapter =
            noClasses().that().resideInAPackage("..adapter.out.persistence..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter.in.web..");
}
