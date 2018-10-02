package com.iteration1.savingwildlife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iteration1.savingwildlife.entities.Beach;


public class InfoTextFragment extends Fragment {
    private View parentView;
    //    private WebView txt;
    private Beach selected;
    private ImageView banner;
    private TextView area;
    private TextView location;
    private TextView f1;
    private TextView f2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.beach_text_fragment, container, false);
        Bundle bundle = this.getArguments();
        assert bundle != null;
        selected = (Beach) bundle.getSerializable("selected");
        banner = (ImageView) parentView.findViewById(R.id.banner);
        area = (TextView) parentView.findViewById(R.id.area);
        location = (TextView) parentView.findViewById(R.id.location);
        f1 = (TextView) parentView.findViewById(R.id.f1);
        f2 = (TextView) parentView.findViewById(R.id.f2);
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(selected.getBanner());
        GlideApp.with(parentView.getContext())
                .load(imageRef)
                .apply((new RequestOptions().placeholder(R.drawable.common_full_open_on_phone).error(R.drawable.common_full_open_on_phone)))
                .into(banner);
//        txt = (WebView) parentView.findViewById(R.id.beachtxt);
//        String putText = "<html><body><p align=\"justify\">";
//        putText += selected.getDescription();
//        putText += "</p></body></html>";
//        txt.loadData(putText, "text/html", "utf-8");
//        txt.getSettings().setTextZoom(100);
//        txt.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.bg_color));
        area.setText(selected.getArea());
        location.setText(selected.getLocation());
        f1.setText(selected.getFeature1());
        f2.setText(selected.getFeature2());
        return parentView;
    }
}
