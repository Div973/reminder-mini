package com.example.reminder.reminder_mini.UserInterfaces.HomePage.MainActivity;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.reminder.mini.UserInterfaces.HomePage.Tasks.Tasks;


public class MainPagePagerAdapter extends FragmentStateAdapter {
    private final Tasks taskEventPage = new Tasks();


    public MainPagePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return taskEventPage;

    }


    @Override
    public int getItemCount() {
        return 1;
    }


}
