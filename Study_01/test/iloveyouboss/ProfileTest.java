package iloveyouboss;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class ProfileTest {
    private Profile profile;
    private Criteria criteria;

    private Question questionReimbursesTuition;
    private Answer answerReimbursesTuition;
    private Answer answerDoesNotReimburseTuition;

    private Question questionIsThereRelocation;
    private Answer answerThereIsRelocation;
    private Answer answerThereIsNoRelocation;

    private Question questionOnsiteDaycare;
    private Answer answerNoOnsiteDaycare;
    private Answer answerHasOnsiteDaycare;

    @Before
    public void createProfile() {
        profile = new Profile("Bull Hockey, Inc.");
    }

    @Before
    public void createCriteria() {
        criteria = new Criteria();
    }

    @Before
    public void createQuestionsAndAnswers() {
        questionIsThereRelocation = new BoooleanQuestion(1, "Relocation package?");
        answerThereIsRelocation = new Answer(questionIsThereRelocation, Boool.TRUE);
        answerThereIsNoRelocation = new Answer(questionIsThereRelocation, Boool.FALSE);

        questionReimbursesTuition = new BoooleanQuestion(1, "Reimburses tuition?");
        answerReimbursesTuition = new Answer(questionReimbursesTuition, Boool.TRUE);
        answerDoesNotReimburseTuition = new Answer(questionReimbursesTuition, Boool.FALSE);

        questionOnsiteDaycare = new BoooleanQuestion(1, "Onsite daycare?");
        answerHasOnsiteDaycare = new Answer(questionOnsiteDaycare, Boool.TRUE);
        answerNoOnsiteDaycare = new Answer(questionOnsiteDaycare, Boool.FALSE);
    }

    @Test
    public void matchAnswersFalseWhenMustMatchCriteriaNotMet() {
        profile.add(answerDoesNotReimburseTuition);
        criteria.add(new Criterion(answerReimbursesTuition, Weight.MustMatch));

        boolean matches = profile.matches(criteria);

        assertFalse(matches);
    }

    @Test
    public void matchAnswersTrueForAnyDontCareCriteria() {
        profile.add(answerDoesNotReimburseTuition);
        criteria.add(new Criterion(answerReimbursesTuition, Weight.DontCare));

        boolean matches = profile.matches(criteria);

        assertTrue(matches);
    }

    @Test
    public void matchAnswersTrueWhenAnyOfMultipleCriteriaMatch() {
        profile.add(answerThereIsRelocation);
        profile.add(answerDoesNotReimburseTuition);
        criteria.add(new Criterion(answerThereIsRelocation, Weight.Important));
        criteria.add(new Criterion(answerReimbursesTuition, Weight.Important));

        boolean matches = profile.matches(criteria);

        assertTrue(matches);
    }

    @Test
    public void matchAnswersFalseWhenNoneOfMultipleCriteriaMatch() {
        profile.add(answerThereIsNoRelocation);
        profile.add(answerDoesNotReimburseTuition);
        criteria.add(new Criterion(answerThereIsRelocation, Weight.Important));
        criteria.add(new Criterion(answerReimbursesTuition, Weight.Important));

        boolean matches = profile.matches(criteria);

        assertFalse(matches);
    }

    @Test
    public void scoreIsZeroWhenThereAreNoMatches() {
        profile.add(answerThereIsNoRelocation);
        criteria.add(new Criterion(answerThereIsRelocation, Weight.Important));

        profile.matches(criteria);

        assertThat(profile.score(), equalTo(0));
    }

    @Test
    public void scoreIsCriterionValueForSingleMatch() {
        profile.add(answerThereIsRelocation);
        criteria.add(new Criterion(answerThereIsRelocation, Weight.Important));

        profile.matches(criteria);

        assertThat(profile.score(), equalTo(Weight.Important.getValue()));
    }

    @Test
    public void scoreAccumulatesCriterionValuesForMatches() {
        profile.add(answerThereIsRelocation);
        profile.add(answerReimbursesTuition);
        profile.add(answerNoOnsiteDaycare);
        criteria.add(new Criterion(answerThereIsRelocation, Weight.Important));
        criteria.add(new Criterion(answerReimbursesTuition, Weight.WouldPrefer));
        criteria.add(new Criterion(answerHasOnsiteDaycare, Weight.VeryImportant));

        profile.matches(criteria);

        int expectedScore = Weight.Important.getValue() + Weight.WouldPrefer.getValue();
        assertThat(profile.score(), equalTo(expectedScore));
    }

    // TODO: missing functionality--what if there is no matching profile answer for a criterion?
}