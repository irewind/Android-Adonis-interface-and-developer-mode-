package com.irewind.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
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

public class IRTabActivity extends Activity implements View.OnClickListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irtab);
        ButterKnife.inject(this);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, IRLibraryFragment.newInstance())
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_irtab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.library:
                if(switchSelection(mLibrary)){
                    switchFragment(IRLibraryFragment.newInstance());
                }
                break;
            case R.id.people:
                if(switchSelection(mPeople)){
                    switchFragment(IRPeopleFragment.newInstance());
                }
                break;
            case R.id.account:
                if(switchSelection(mAccount)){
                    switchFragment(IRAccountFragment.newInstance());
                }
                break;
            case R.id.more:
                if(switchSelection(mMore)){
                    switchFragment(IRMoreFragment.newInstance());
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
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(0, R.anim.enter);
        ft.add(R.id.container, fragment)
          .disallowAddToBackStack()
          .commit();
    }
}
