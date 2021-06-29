package com.sixfourapps.use;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sixfourapps.use.databinding.AppCardBinding;

import java.util.List;

public class AppCardAdapter extends RecyclerView.Adapter<AppCardAdapter.AppCardViewHolder> {

    public interface OnAppCardClickListener {
        void onAppCardClick(int position);

        void onAppCardLongClick(int position);
    }

    private List<AppInfoWrapper> appList;
    private OnAppCardClickListener listener;
    private MainActivity mainActivity;

    public AppCardAdapter() {
    }

    public AppCardAdapter(MainActivity mainActivity, List<AppInfoWrapper> appList, OnAppCardClickListener listener) {
        this.mainActivity = mainActivity;
        this.appList = appList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public AppCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AppCardBinding binding = AppCardBinding.inflate(inflater, parent, false);
        AppCardViewHolder viewHolder = new AppCardViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppCardViewHolder holder, int position) {
        AppInfoWrapper appInfo = appList.get(position);
        holder.binding.setAppInfo(appInfo);

        //change background color based on selection
        if (appInfo.isSelected()) {
            switch (mainActivity.focusMode) {
                case USE_FULL:
                    holder.itemView.setBackgroundColor(mainActivity.getResources().getColor(
                            R.color.green_200,
                            mainActivity.getTheme()
                    ));
                    break;
                case USE_LESS:
                    holder.itemView.setBackgroundColor(mainActivity.getResources().getColor(
                            R.color.red_200,
                            mainActivity.getTheme()
                    ));
                    break;
            }
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        //call onClickListener from MainActivity
        holder.itemView.setOnClickListener(v -> {
            listener.onAppCardClick(position);
        });

        //call onLongClickListener from MainActivity
        holder.itemView.setOnLongClickListener(v -> {
            listener.onAppCardLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public class AppCardViewHolder extends RecyclerView.ViewHolder {
        private AppCardBinding binding;

        public AppCardViewHolder(@NonNull AppCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
