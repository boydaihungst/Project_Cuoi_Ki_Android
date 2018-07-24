package com.example.dragon.project_cuoi_ki_android.player;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.Utils.Utils;
import com.example.dragon.project_cuoi_ki_android.model.Song;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerTabFragment extends Fragment {
    private Utils utils;

    public PlayerTabFragment() {
        utils = new Utils(getContext());
    }

    private ImageView player_icon_center;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_second_tab, container, false);
        player_icon_center = (ImageView) view.findViewById(R.id.player_icon_center);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setData(Song song, boolean isStopAnimation) {
        if (song != null) {
            if (player_icon_center != null) {
                ((BitmapDrawable) player_icon_center.getDrawable()).getBitmap().recycle();
                if (song.getPicture() == null) {
                    player_icon_center.setImageBitmap(utils.resize(this.getActivity().getDrawable(R.drawable.demo), player_icon_center));
                } else {
                    player_icon_center.setImageBitmap(utils.resize(new BitmapDrawable(getResources(), song.getPicture()), player_icon_center));
                }
                rotateAnimationForImg(player_icon_center);
            }
        }
        try {
            if (isStopAnimation) {
                ObjectAnimator animator = (ObjectAnimator) player_icon_center.getTag();
                animator.pause();
            } else {
                ObjectAnimator animator = (ObjectAnimator) player_icon_center.getTag();
                animator.pause();
                animator.resume();
            }
        } catch (Exception e) {
        }
    }

    private void rotateAnimationForImg(ImageView image) {
        ObjectAnimator rotateAnimation = (ObjectAnimator) image.getTag();
        if (rotateAnimation == null || !(image.getTag() instanceof ObjectAnimator))
            rotateAnimation = ObjectAnimator.ofFloat(image,
                    "rotation", 0f, 360f);
        rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(20000);
        rotateAnimation.start();
        image.setTag(rotateAnimation);
    }

}
