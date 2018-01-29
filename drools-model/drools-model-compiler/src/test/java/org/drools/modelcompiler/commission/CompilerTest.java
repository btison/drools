package org.drools.modelcompiler.commission;

import org.drools.modelcompiler.BaseModelTest;
import org.drools.modelcompiler.commission.model.Customer;
import org.drools.modelcompiler.commission.model.TargetPolicy;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompilerTest extends BaseModelTest {

    public CompilerTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testTargetPolicyCoefficient() {

        String str=
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + TargetPolicy.class.getCanonicalName() + ";\n" +
                "import " + List.class.getCanonicalName() + ";\n" +
                "import " + ArrayList.class.getCanonicalName() + ";\n" +
                "rule \"Customer can only have one Target Policy for Product p1 with coefficient 1\"\n" +
                "when\n" +
                "$customer : Customer( $code : code )\n" +
                "$target : TargetPolicy( customerCode == $code, productCode == \"p1\", coefficient == 1 )\n" +
                "ArrayList(size > 1) from collect ( TargetPolicy( customerCode == $code, productCode == \"p1\", coefficient == 1) )\n" +
                "then\n" +
                "$target.setCoefficient(0);\n" +
                "update($target);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Customer customer = new Customer();
        customer.setCode("code1");
        TargetPolicy target1 = new TargetPolicy();
        target1.setCustomerCode("code1");
        target1.setProductCode("p1");
        target1.setCoefficient(1);
        TargetPolicy target2 = new TargetPolicy();
        target2.setCustomerCode("code1");
        target2.setProductCode("p1");
        target2.setCoefficient(1);
        TargetPolicy target3 = new TargetPolicy();
        target3.setCustomerCode("code1");
        target3.setProductCode("p1");
        target3.setCoefficient(1);

        ksession.insert(customer);
        ksession.insert(target1);
        ksession.insert(target2);
        ksession.insert(target3);
        ksession.fireAllRules();

        List<TargetPolicy> targetPolicyList = Arrays.asList(target1, target2, target3);
        long filtered = targetPolicyList.stream().filter(c -> c.getCoefficient() == 1).count();
        assertEquals(1, filtered);

        ksession.dispose();
    }

}
