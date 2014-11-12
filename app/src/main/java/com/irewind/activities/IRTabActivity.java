package com.irewind.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.irewind.R;
import com.irewind.fragments.IRAccountFragment;
import com.irewind.fragments.IRLibraryFragment;
import com.irewind.fragments.IRMoreFragment;
import com.irewind.fragments.IRPeopleFragment;
import com.irewind.fragments.IRRewindFunctionalityFragment;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRTabActivity extends ActionBarActivity implements View.OnClickListener{

    @InjectView(R.id.circle)
    CircleButton mCircleButton;
    @InjectView(R.id.library)
    ImageButton mLibrary;
    @InjectView(R.id.people)
    ImageButton mPeople;
    @InjectView(R.id.account)
    ImageButton mAccount;
    @InjectView(R.id.more)
    ImageButton mMore;

    private Fragment mLibraryFragment, mPeopleFragment, mAccountFragment, mMoreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irtab);
        ButterKnife.inject(this);
        if (savedInstanceState == null) {
            mLibraryFragment = IRLibraryFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mLibraryFragment)
                    .disallowAddToBackStack()
                    .commit();
            mLibrary.setSelected(true);
        }

        mLibrary.setOnClickListener(this);
        mPeople.setOnClickListener(this);
        mAccount.setOnClickListener(this);
        mMore.setOnClickListener(this);
        mCircleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.library:
                if(switchSelection(mLibrary)){
                    if (mLibraryFragment == null){
                        mLibraryFragment = IRLibraryFragment.newInstance();
                    }
                    switchFragment(mLibraryFragment);
                }
                break;
            case R.id.people:
                if(switchSelection(mPeople)){
                    if (mPeopleFragment == null){
                        mPeopleFragment = IRPeopleFragment.newInstance();
                    }
                    switchFragment(mPeopleFragment);
                }
                break;
            case R.id.account:
                if(switchSelection(mAccount)){
                    if (mAccountFragment == null){
                        mAccountFragment = IRAccountFragment.newInstance();
                    }
                    switchFragment(mAccountFragment);
                }
                break;
            case R.id.more:
                if(switchSelection(mMore)){
                    if (mMoreFragment == null){
                        mMoreFragment = IRAccountFragment.newInstance();
                    }
                    switchFragment(mMoreFragment);
                }
                break;
            case R.id.circle:
                if(switchSelectionMiddle(mCircleButton)){
                    switchFragment(IRRewindFunctionalityFragment.newInstance());
                }
                break;
        }
    }

    private boolean switchSelection(ImageButton btn){
        if (!btn.isSelected()){
            btn.setSelected(true);
            if (btn != mAccount){
                mAccount.setSelected(false);
            }
            if (btn != mLibrary){
                mLibrary.setSelected(false);
            }
            if (btn != mMore){
                mMore.setSelected(false);
            }
            if (btn != mPeople){
                mPeople.setSelected(false);
            }
            mCircleButton.setSelected(false);
            return true;
        }
        return false;
    }

    private boolean switchSelectionMiddle(CircleButton btn){
        if (!btn.isSelected()){
            btn.setSelected(true);
            mAccount.setSelected(false);
            mLibrary.setSelected(false);
            mMore.setSelected(false);
            mPeople.setSelected(false);
            return true;
        }
        return false;
    }

    private void switchFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment)
          .disallowAddToBackStack()
          .commit();
    }
}
