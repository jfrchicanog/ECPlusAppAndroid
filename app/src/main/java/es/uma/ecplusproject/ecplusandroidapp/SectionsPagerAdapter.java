package es.uma.ecplusproject.ecplusandroidapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Panel[] sindromes;

    public SectionsPagerAdapter(FragmentManager fm, Panel... sindromes) {
        super(fm);
        this.sindromes = sindromes;
    }

    @Override
    public Fragment getItem(int position) {
        return sindromes[position];
    }

    @Override
    public int getCount() {
        return sindromes.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sindromes[position].getFragmentName();
    }
}
