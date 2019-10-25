/*
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy of
 *   this software and associated documentation files (the "Software"), to deal in
 *   the Software without restriction, including without limitation the rights to
 *   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *   the Software, and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *   FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *   COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *   IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.rebase.rebot.plugin.welcome.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import io.quarkus.test.junit.QuarkusTest;
import it.rebase.rebot.plugin.welcome.WelcomeMessagePlugin;
import it.rebase.rebot.plugin.welcome.kogito.WelcomeChallenge;
import org.jbpm.process.instance.impl.humantask.HumanTaskTransition;
import org.jbpm.process.instance.impl.humantask.phases.Claim;
import org.jbpm.process.instance.impl.workitem.Complete;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.services.identity.StaticIdentityProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class WelcomeProcessTest {

    @Inject
    @Named("welcome.challenge")
    Process<? extends Model> welcomeProcess;

    @Inject
    WelcomeMessagePlugin welcome;

    @BeforeAll
    public static void prepre() {
        System.setProperty("it.rebase.rebot.telegram.token", "faketoken");
        System.setProperty("it.rebase.rebot.telegram.userId", "fakeBotID");
    }


    @Test
    public void testWelcomeChallengeProcessTimeout() throws InterruptedException {

        assertNotNull(welcomeProcess);

        WelcomeChallenge challenge = new WelcomeChallenge("spolti");

        Model model = welcomeProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("challenge", challenge);
        model.fromMap(parameters);

        ProcessInstance<?> processInstance = welcomeProcess.createInstance(model);
        processInstance.start();

        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.status());

        Thread.sleep(15000);

        Model result = (Model) processInstance.variables();
        assertEquals(1, result.toMap().size());

        List<WorkItem> workItems = processInstance.workItems();
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID, result.toMap()));
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status());
        assertEquals(true, ((WelcomeChallenge) result.toMap().get("challenge")).isKickUser());
    }

    @Test
    public void testWelcomeChallengeProcessCorrectChallengeAnswer() {
        assertNotNull(welcomeProcess);

        WelcomeChallenge challenge = new WelcomeChallenge("test-1");

        Model model = welcomeProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("challenge", challenge);
        model.fromMap(parameters);

        ProcessInstance<?> processInstance = welcomeProcess.createInstance(model);
        processInstance.start();
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.status());
        SecurityPolicy policy = welcome.securityProviderForUser("spolti-1");

        processInstance.workItems(policy);

        List<WorkItem> workItems = processInstance.workItems(policy);

        System.out.println( processInstance.workItems(policy));
        System.out.println( processInstance.workItems());
        assertEquals(1, workItems.size());

        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Claim.ID, parameters, policy));

        Model result = (Model) processInstance.variables();
        assertEquals(1, result.toMap().size());

        Map<String, Object> results = new HashMap<>();
        challenge = ((WelcomeChallenge) result.toMap().get("challenge"));

        challenge.setAnswer(Common.challengeResult(challenge));
        results.put("challenge", challenge);

        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID, results, policy));

        Model finalResult = (Model) processInstance.variables();
        assertEquals(false, ((WelcomeChallenge) finalResult.toMap().get("challenge")).isKickUser());

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status());
    }

    @Test
    public void testWelcomeChallengeProcessWrongChallengeAnswer() {

        assertNotNull(welcomeProcess);

        WelcomeChallenge challenge = new WelcomeChallenge("spolti-2");

        Model model = welcomeProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("challenge", challenge);
        model.fromMap(parameters);

        ProcessInstance<?> processInstance = welcomeProcess.createInstance(model);
        processInstance.start();
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.status());

        SecurityPolicy policy = welcome.securityProviderForUser("spolti-2");
        processInstance.workItems(policy);

        List<WorkItem> workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());

        Model result = (Model) processInstance.variables();
        assertEquals(1, result.toMap().size());

        Map<String, Object> results = new HashMap<>();
        challenge = ((WelcomeChallenge) result.toMap().get("challenge"));

        challenge.setAnswer(Common.challengeResult(challenge));
        results.put("challenge", challenge);

        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID, results, policy));

        Model finalResult = (Model) processInstance.variables();
        assertEquals(false, ((WelcomeChallenge) finalResult.toMap().get("challenge")).isKickUser());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status());
    }

    // next test claim the tasks and finish the processes.
    @Test
    public void testProcessWithUserClaim() throws InterruptedException {
        assertNotNull(welcomeProcess);

        String user1 = "user-1";
        String user2 = "user-2";
        String user3 = "user-3";
        String user4 = "user-4";

        WelcomeChallenge challengeUser1 = new WelcomeChallenge(user1);
        assertNotNull(challengeUser1);
        WelcomeChallenge challengeUser2 = new WelcomeChallenge(user2);
        assertNotNull(challengeUser2);
        WelcomeChallenge challengeUser3 = new WelcomeChallenge(user3);
        assertNotNull(challengeUser3);
        WelcomeChallenge challengeUser4 = new WelcomeChallenge(user4);
        assertNotNull(challengeUser4);

        Model modelUser1 = welcomeProcess.createModel();
        Map<String, Object> user1Params = new HashMap<>();
        user1Params.put("challenge", challengeUser1);
        modelUser1.fromMap(user1Params);

        Model modelUser2 = welcomeProcess.createModel();
        Map<String, Object> user2Params = new HashMap<>();
        user2Params.put("challenge", challengeUser2);
        modelUser2.fromMap(user2Params);

        Model modelUser3 = welcomeProcess.createModel();
        Map<String, Object> user3Params = new HashMap<>();
        user3Params.put("challenge", challengeUser3);
        modelUser3.fromMap(user3Params);

        Model modelUser4 = welcomeProcess.createModel();
        Map<String, Object> user4Params = new HashMap<>();
        user4Params.put("challenge", challengeUser4);
        modelUser4.fromMap(user4Params);

        ProcessInstance<?> processInstanceUser1 = welcomeProcess.createInstance(modelUser1);
        processInstanceUser1.start();
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceUser1.status());
        SecurityPolicy policyUser1 = welcome.securityProviderForUser(user1);
        List<WorkItem> workItemsUser1 = processInstanceUser1.workItems();
        processInstanceUser1.transitionWorkItem(workItemsUser1.get(0).getId(), new HumanTaskTransition(Claim.ID, user1Params, policyUser1));

        ProcessInstance<?> processInstanceUser2 = welcomeProcess.createInstance(modelUser2);
        processInstanceUser2.start();
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceUser2.status());
        SecurityPolicy policyUser2 = welcome.securityProviderForUser(user2);
        List<WorkItem> workItemsUser2 = processInstanceUser2.workItems();
        processInstanceUser2.transitionWorkItem(workItemsUser2.get(0).getId(), new HumanTaskTransition(Claim.ID, user2Params, policyUser2));

        ProcessInstance<?> processInstanceUser3 = welcomeProcess.createInstance(modelUser3);
        processInstanceUser3.start();
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceUser3.status());
        SecurityPolicy policyUser3 = welcome.securityProviderForUser(user3);
        List<WorkItem> workItemsUser3 = processInstanceUser3.workItems();
        processInstanceUser3.transitionWorkItem(workItemsUser3.get(0).getId(), new HumanTaskTransition(Claim.ID, user3Params, policyUser3));

        ProcessInstance<?> processInstanceUser4 = welcomeProcess.createInstance(modelUser4);
        processInstanceUser4.start();
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceUser4.status());
        SecurityPolicy policyUser4 = welcome.securityProviderForUser(user4);
        List<WorkItem> workItemsUser4 = processInstanceUser4.workItems();
        processInstanceUser4.transitionWorkItem(workItemsUser4.get(0).getId(), new HumanTaskTransition(Claim.ID, user4Params, policyUser4));

        Thread.sleep(1000);
        Model challengeUser4ModelResult = (Model) processInstanceUser4.variables();
        assertEquals(1, challengeUser4ModelResult.toMap().size());
        challengeUser4 = ((WelcomeChallenge) challengeUser4ModelResult.toMap().get("challenge"));
        processInstanceUser4.transitionWorkItem(workItemsUser4.get(0).getId(), new HumanTaskTransition(Complete.ID, user4Params, policyUser4));
        assertEquals(true, challengeUser4.isKickUser());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceUser4.status());

        Thread.sleep(1000);
        Model challengeUser1ModelResult = (Model) processInstanceUser1.variables();
        assertEquals(1, challengeUser1ModelResult.toMap().size());
        Map<String, Object> challengeUser1Result = new HashMap<>();
        challengeUser1 = ((WelcomeChallenge) challengeUser1ModelResult.toMap().get("challenge"));
        challengeUser1.setAnswer(Common.challengeResult(challengeUser1));
        challengeUser1Result.put("challenge", challengeUser1);
        processInstanceUser1.transitionWorkItem(workItemsUser1.get(0).getId(), new HumanTaskTransition(Complete.ID, challengeUser1Result, policyUser1));
        assertEquals(false, challengeUser1.isKickUser());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceUser1.status());

        Thread.sleep(1000);
        Model challengeUser2ModelResult = (Model) processInstanceUser2.variables();
        assertEquals(1, challengeUser2ModelResult.toMap().size());
        Map<String, Object> challengeUser2Result = new HashMap<>();
        challengeUser2 = ((WelcomeChallenge) challengeUser2ModelResult.toMap().get("challenge"));
        challengeUser2.setAnswer(Common.challengeResult(challengeUser2));
        challengeUser2Result.put("challenge", challengeUser2);
        processInstanceUser2.transitionWorkItem(workItemsUser2.get(0).getId(), new HumanTaskTransition(Complete.ID, challengeUser2Result, policyUser2));
        assertEquals(false, challengeUser2.isKickUser());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceUser2.status());

        Thread.sleep(1000);
        Model challengeUser3ModelResult = (Model) processInstanceUser3.variables();
        assertEquals(1, challengeUser3ModelResult.toMap().size());
        Map<String, Object> challengeUser3Result = new HashMap<>();
        challengeUser3 = ((WelcomeChallenge) challengeUser3ModelResult.toMap().get("challenge"));
        challengeUser3.setAnswer(00000000000);
        challengeUser3Result.put("challenge", challengeUser3);
        processInstanceUser3.transitionWorkItem(workItemsUser3.get(0).getId(), new HumanTaskTransition(Complete.ID, challengeUser3Result, policyUser3));
        assertEquals(true, challengeUser3.isKickUser());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceUser3.status());
    }
}
