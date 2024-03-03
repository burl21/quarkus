package io.quarkus.it.liquibase;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@DisplayName("Tests liquibase extension")
public class LiquibaseFunctionalityTest {

    @Test
    @DisplayName("Migrates a schema correctly using integrated instance")
    public void testLiquibaseQuarkusFunctionality() {
        doTestLiquibaseQuarkusFunctionality(isIncludeAllExpectedToWork());
    }

    @Test
    @DisplayName("Migrates a schema correctly using dedicated username and password from config properties")
    @DisabledOnOs(value = OS.WINDOWS, disabledReason = "Our Windows CI does not have Docker installed properly")
    public void testLiquibaseUsingDedicatedUsernameAndPassword() {
        when().get("/liquibase/updateWithDedicatedUser").then().body(is(
                "create-quarkus-table,insert-into-quarkus-table"));

        when().get("/liquibase/created-by").then().body(is(
                "USR"));
    }

    static void doTestLiquibaseQuarkusFunctionality(boolean isIncludeAllExpectedToWork) {
        when()
                .get("/liquibase/update")
                .then()
                .body(is(
                        "create-tables-1,test-1,create-view-inline,create-view-file-abs,create-view-file-rel,"
                                + (isIncludeAllExpectedToWork ? "includeAll-1,includeAll-2," : "")
                                + "json-create-tables-1,json-test-1,"
                                + "sql-create-tables-1,sql-test-1,"
                                + "yaml-create-tables-1,yaml-test-1,"
                                + "00000000000000,00000000000001,00000000000002,"
                                + "1613578374533-1,1613578374533-2,1613578374533-3"));
    }

    protected boolean isIncludeAllExpectedToWork() {
        return true;
    }
}
