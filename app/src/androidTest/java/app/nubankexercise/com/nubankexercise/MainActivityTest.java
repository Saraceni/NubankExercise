package app.nubankexercise.com.nubankexercise;

import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Created by rafaelgontijo on 6/27/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;
    ViewPager pager;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        activity = getActivity();
    }

    @SmallTest
    public void testPagerNotNull()
    {
        pager = (ViewPager) activity.findViewById(R.id.pager);
        assertNotNull(null);
    }
}
