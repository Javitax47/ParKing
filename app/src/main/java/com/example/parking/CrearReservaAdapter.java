package com.example.parking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import java.util.List;

public class CrearReservaAdapter extends PagerAdapter {

    private List<View> pages;

    public CrearReservaAdapter(List<View> pages) {
        this.pages = pages;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View page = pages.get(position);

        // Maneja la última página de manera diferente
        if (position == getCount() - 1) {
            // Infla la última página desde otro diseño XML
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            page = inflater.inflate(R.layout.reserva_sin_vehiculo, container, false);
            // Configura elementos específicos de la última página
        }

        container.addView(page);
        return page;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
