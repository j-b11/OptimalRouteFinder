package com.brian.zaplanujwyjazd;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class MyListAdapter extends BaseAdapter implements android.widget.ListAdapter {
    private List<Place> selectedPlaces = new ArrayList<>();
    private int layoutResId;
    private Context context;

    MyListAdapter(List<Place> selectedPlaces, int layoutResId, Context context) {
        this.selectedPlaces = selectedPlaces;
        this.layoutResId = layoutResId;
        this.context = context;
    }

    @Override
    public int getCount() {
        return selectedPlaces.size();
    }

    @Override
    public Object getItem(int pos) {
        return selectedPlaces.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //return 0 if list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(layoutResId, null);
        }

        //Handle TextViews
        TextView listItemName = view.findViewById(R.id.list_item_name);
        TextView listItemDescription = view.findViewById(R.id.list_item_description);
        listItemName.setText(selectedPlaces.get(position).getName());

        switch (layoutResId){
            case(R.layout.selected_places_list_element):
                String name = (position + 1) + ". " + selectedPlaces.get(position).getName();
                listItemName.setText(name);

                listItemDescription.setText(selectedPlaces.get(position).getDescription());

                Button deleteBtn = view.findViewById(R.id.delete_btn);
                AssetManager assetManager = context.getAssets();
                Typeface font = Typeface.createFromAsset( assetManager, "ionicons.ttf" );
                deleteBtn.setTypeface(font);

                deleteBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        selectedPlaces.remove(position);
                        notifyDataSetChanged();
                    }
                });

                break;

            case (R.layout.found_places_list_element):
                listItemName.setText(selectedPlaces.get(position).getName());

                String description = selectedPlaces.get(position).getDescription();
                if(selectedPlaces.get(position).getRating() != 0.0){
                    description = description + "\n" + context.getString(R.string.rating) +
                            selectedPlaces.get(position).getRating() + "/5";
                }
                listItemDescription.setText(description);
                break;
        }

        return view;
    }
}