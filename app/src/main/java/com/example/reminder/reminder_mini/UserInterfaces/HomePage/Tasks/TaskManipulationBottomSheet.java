package com.example.reminder.reminder_mini.UserInterfaces.HomePage.Tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TaskData;

public class TaskManipulationBottomSheet extends BottomSheetDialogFragment {
    private final TaskData taskData;

    private final ApplicationCustomInterfaces.ManipulateTask manipulateTask;

    public TaskManipulationBottomSheet(TaskData taskData, ApplicationCustomInterfaces.ManipulateTask manipulateTask) {
        this.manipulateTask = manipulateTask;
        this.taskData = taskData;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_manipulation_bottom_sheet, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        MaterialButton deleteButton = view.findViewById(R.id.deleteTask);
        MaterialButton pinButton = view.findViewById(R.id.pinTask);
        MaterialButton markDoneButton = view.findViewById(R.id.markTaskDone);
        MaterialButton editButton = view.findViewById(R.id.rescheduleTask);
        MaterialButton lockButton = view.findViewById(R.id.lockTask);

        markDoneButton.setOnClickListener(v -> manipulateTask.markTaskDone());
        lockButton.setOnClickListener(v -> manipulateTask.lockTask());
        deleteButton.setOnClickListener(v -> manipulateTask.deleteTask());
        pinButton.setOnClickListener(v -> manipulateTask.pinTask());
        editButton.setOnClickListener(v -> manipulateTask.reschedule());


        if (taskData.getPinned() == TaskConstants.PINNED_YES) {
            pinButton.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.unpin));
            pinButton.setText(getString(R.string.unpin));
        }

        if (taskData.getAlreadyDone() == TaskConstants.ALREADY_DONE_YES) {
            markDoneButton.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.remove_done));
            markDoneButton.setText(getString(R.string.markNotDone));
        }

        if (taskData.getPrivateTask() == TaskConstants.PRIVATE_YES) {
            lockButton.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_lock_open));
            lockButton.setText(getString(R.string.unlock));
        }


        //view.findViewById(R.id.priorityTask).setOnClickListener(v-> manipulateTask.priorityTask());

    }
}
