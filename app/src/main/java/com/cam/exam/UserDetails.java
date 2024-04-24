package com.cam.exam;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.squareup.picasso.Picasso;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserDetails extends AppCompatActivity {

    private TextView firstName;
    private TextView lastName;
    private ImageView imageUrl;
    private TextView birthDate;
    private TextView age;
    private TextView email;
    private TextView mobile;
    private TextView address;
    private TextView contactPerson;
    private TextView contactPersonMobile;
    private TextView tv_emailProfile;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_details);

        firstName = findViewById(R.id.tv_name);
        lastName = findViewById(R.id.tv_name);
        birthDate = findViewById(R.id.tv_birthdate);
        address = findViewById(R.id.tv_address);
        contactPerson = findViewById(R.id.tv_contactPerson);
        age = findViewById(R.id.tv_age);
        mobile = findViewById(R.id.tv_mobile);
        imageUrl = findViewById(R.id.imageView_image);
        tv_emailProfile = findViewById(R.id.tv_emailProfile);
        Button backButton = findViewById(R.id.button_toolbar);
    //    contactPerson = findViewById(R.id.cont)

        UserData user = (UserData) getIntent().getSerializableExtra("user");

        if (user != null) {
            String birthdateFormatted = getFormatBirthday(user.getBirthDate());

            firstName.setText(user.getFullName());
            birthDate.setText(birthdateFormatted);
            address.setText(user.getAddress());
            age.setText(user.getAge());
            contactPerson.setText(user.getContactPersonMobile());
            mobile.setText(user.getMobile());
            imageUrl.setImageURI(Uri.parse(user.getImageUrl()));
            tv_emailProfile.setText(user.getEmail());

            Picasso.get()
                    .load(user.getImageUrl())
                    .transform(new CropCircleTransformation()) // Apply circular transformation
                    .into(imageUrl);

        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private String getFormatBirthday(String _date) {
        String inputDate = _date;
        String formattedDate = "";

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

        try {
            Date date = inputFormat.parse(inputDate);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}

