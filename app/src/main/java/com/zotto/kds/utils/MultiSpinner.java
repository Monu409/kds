
package com.zotto.kds.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.zotto.kds.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MultiSpinner extends AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    private List items;
    //    private Boolean[] selected;
    private ArrayList<Boolean> selected;
    private String defaultText;
    private MultiSpinnerListener listener;
    boolean is_SingleSelect = false;
    MultiSpinner clild_spiner;

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//        selected[which] = isChecked;
        selected.set(which, isChecked);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        setSelected_Text();
    }

    public void setSelected_Text() {
        StringBuilder spinnerBuffer = new StringBuilder();
        boolean someUnselected;
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                if (selected.get(i)) {
                    if (spinnerBuffer.length() > 2)
                        spinnerBuffer.append(", ");
                    spinnerBuffer.append(items.get(i).toString());
                }
            }
            someUnselected = !areAllFalse(selected);

            String spinnerText;
            if (someUnselected) {
                spinnerText = spinnerBuffer.toString();
//            if (spinnerText.length() > 2)
//                spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
            } else {
                spinnerText = defaultText;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spiner_tv, R.id.spnr_text, new String[]{spinnerText});
            adapter.setDropDownViewResource(R.layout.spiner_item);
            setAdapter(adapter);
            Boolean[] itms = new Boolean[selected.size()];
            itms = selected.toArray(itms);
//        Boolean[] itms = (Boolean[]) selected.toArray(new Boolean[selected.size()]);
            listener.onItemsSelected(itms);
        }
    }


    //    public static boolean areAllFalse(Boolean[] array) {
    public static boolean areAllFalse(ArrayList<Boolean> array) {
        for (boolean b : array) if (b) return false;
        return true;
    }

    public void setChildSpinner(MultiSpinner childSpinner) {
        this.clild_spiner = childSpinner;
    }

    public MultiSpinner getChildSpinner() {
        return clild_spiner;
    }

    public void setSingle_select(boolean is_SingleSelect) {
        this.is_SingleSelect = is_SingleSelect;
    }

    public void setListener(MultiSpinnerListener listener) {
        this.listener = listener;
    }

    public MultiSpinnerListener getListener() {
        return listener;
    }

    @Override
    public boolean performClick() {
        final Dialog dialog = new Dialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog1);

        TextView text = dialog.findViewById(R.id.txt_dia);
        EditText editSearch = dialog.findViewById(R.id.editSearch);
        AppCompatButton btn_ok = dialog.findViewById(R.id.btn_ok);
        text.setText(defaultText);

        final ListAdapter ad = new ListAdapter(getContext(), items, selected);
        ListView listView = dialog.findViewById(R.id.item_list);
        listView.setAdapter(ad);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (is_SingleSelect) {
                    Collections.fill(selected, false);
                    selected.set(position, true);
                    ad.notifyDataSetChanged();
                } else {
                    int actualPos = ad.getActualpos(position);
                    if (actualPos != -1) {
                        position = actualPos;
                    }
                    if (selected.get(position)) {
                        selected.set(position, false);
                    } else {
                        selected.set(position, true);
                    }
                }
                ((CheckBox) view.findViewById(R.id.check_btn)).setChecked(selected.get(position));
            }
        });
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    ad.updateData(items, selected);
                } else {
                    List slctd_items = new ArrayList();
                    ArrayList<Boolean> slctd = new ArrayList<Boolean>(Collections.nCopies(items.size(), false));
//                    ArrayList<Boolean> slctd = new Boolean[items.size()];
                    int[] actual_pos = new int[items.size()];
                    for (int k = 0; k < items.size(); k++) {
                        String item = items.get(k).toString();
                        if (item.toLowerCase().contains(s.toString().toLowerCase())) {
                            slctd.set(slctd_items.size(), selected.get(k));
                            actual_pos[slctd_items.size()] = k;
                            slctd_items.add(items.get(k));
                        }
                    }
                    if (ad != null) {
                        ad.updateDataPos(slctd_items, actual_pos, slctd);
                    }
                }
            }
        });
        dialog.setOnCancelListener(this);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        return true;
    }

    public void setItems(List items, List<String> last_selected, String allText, MultiSpinnerListener listener) {
        this.items = items;
        this.defaultText = allText;
        this.listener = listener;

        // all selected by default
//        selected = new Boolean[items.size()];
        selected = new ArrayList<Boolean>(Collections.nCopies(items.size(), false));
        Collections.fill(selected, false);
        move_selected_Top(items, last_selected);
//        for (int i = 0; i < selected.length; i++) {
//            if (last_selected != null)
//                selected[i] = last_selected.contains(items.get(i).toString().trim());
//        }
        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spiner_tv, R.id.spnr_text, new String[]{allText});
        adapter.setDropDownViewResource(R.layout.spiner_item);
        setAdapter(adapter);
        setSelected_Text();
    }

    public void setItems(List items, String allText, MultiSpinnerListener listener) {
        this.items = items;
        this.defaultText = allText;
        this.listener = listener;
        selected = new ArrayList<Boolean>(Collections.nCopies(items.size(), false));
        Collections.fill(selected, false);
//        for (int i = 0; i < selected.length; i++)
//            selected[i] = false;

        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spiner_tv, R.id.spnr_text, new String[]{allText});
        adapter.setDropDownViewResource(R.layout.spiner_item);
        setAdapter(adapter);
    }

    public interface MultiSpinnerListener {
        void onItemsSelected(Boolean[] selected);
    }


    public void move_selected_Top(List items, List<String> last_selected) {
        if (last_selected != null && last_selected.size() > 0) {
            List duplicates = new ArrayList<>();
            for (int i = selected.size() - 1; i >= 0; i--) {
                if (last_selected.contains(items.get(i).toString().trim())) {
                    selected.set(duplicates.size(), true);
                    duplicates.add(items.get(i));
                    items.remove(i);
                }
            }
            items.addAll(0, duplicates);
        }
    }

    public class ListAdapter extends BaseAdapter {
        Context context;
        List items;
        ArrayList<Boolean> selected;
        int[] actual_pos;

        ListAdapter(Context context, List items, ArrayList<Boolean> selected) {
            this.context = context;
            this.items = items;
            this.actual_pos = null;
            this.selected = selected;
        }

        public void updateData(List items, ArrayList<Boolean> selected) {
            this.items = items;
            this.selected = selected;
            this.actual_pos = null;
            notifyDataSetChanged();
        }

        public void updateDataPos(List items, int[] actual_pos, ArrayList<Boolean> selected) {
            this.items = items;
            this.selected = selected;
            this.actual_pos = actual_pos;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (items != null)
                return items.size();
            else
                return 0;
        }

        public int getActualpos(int searchedPos) {
            if (actual_pos != null) {
                return actual_pos[searchedPos];
            } else
                return -1;
        }

        @Override
        public Object getItem(int i) {
            return items.get(i).toString();
        }

        @Override
        public long getItemId(int position) {
            return items.size();
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View v;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.spiner_item, null);
            Object item = items.get(position);
            TextView textView = v.findViewById(R.id.spnr_text);
            textView.setText(item.toString());
            ((CheckBox) v.findViewById(R.id.check_btn)).setChecked(selected.get(position));
            return v;
        }


    }

}
