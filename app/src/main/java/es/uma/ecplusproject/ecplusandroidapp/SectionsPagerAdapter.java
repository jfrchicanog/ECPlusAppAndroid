package es.uma.ecplusproject.ecplusandroidapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import es.uma.ecplusproject.ecplusandroidapp.fragments.Panel;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Panel[] paneles;

    public SectionsPagerAdapter(FragmentManager fm, Panel... paneles) {
        super(fm);
        this.paneles = paneles;
    }

    @Override
    public Fragment getItem(int position) {
        return paneles[position];
    }

    @Override
    public int getCount() {
        return paneles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return paneles[position].getFragmentName();
    }
}
