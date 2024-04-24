package com.cam.exam;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserData> userList;
    public UserAdapter(List<UserData> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        UserData user = userList.get(position);
        holder.bind(user);

        holder.tv_fullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, UserDetails.class);
                intent.putExtra("user", user); // Pass user object to UserDetailsActivity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_fullname;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_fullname = itemView.findViewById(R.id.tv_name);
//          imageViewAvatar = itemView.findViewById(R.id.imageView_image);
        }

        public void bind(UserData user) {
            tv_fullname.setText(user.getFullName());

        }
    }
}
