package in.weoto.gymoto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.weoto.gymoto.GetterSetter.EnquiryList;
import in.weoto.gymoto.GetterSetter.Member;
import in.weoto.gymoto.ListEnquiry;
import in.weoto.gymoto.R;
import in.weoto.gymoto.SingleEnquiryActivity;
import in.weoto.gymoto.SingleMemberActivity;

public class ListEnquiryAdapter extends RecyclerView.Adapter<ListEnquiryAdapter.ViewHolder> {

    ArrayList<EnquiryList> enquirylist;
    Context context;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public ListEnquiryAdapter(ArrayList<EnquiryList> enquirylist, Context context) {
        this.enquirylist = enquirylist;
        this.context = context;
    }

    @NonNull
    @Override
    public ListEnquiryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_list_enquiry_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ListEnquiryAdapter.ViewHolder holder, final int position) {
        holder.mTextView.setText(enquirylist.get(position).getName());
        holder.mTextViewdate.setText(enquirylist.get(position).getDate());

        holder.mImageViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = enquirylist.get(position).getPhone();
                if(phone.equals("")) {
                    Toast.makeText(context, "No Phone Number Available", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String phoneNumber ="tel:"+ phone;
                    Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                }

            }
        });

        holder.recycler_enquirylist_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(context, members.get(position).getMemID(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,SingleEnquiryActivity.class);
                intent.putExtra("ID", enquirylist.get(position).getId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        if(enquirylist != null) {
            return enquirylist.size();
        }
        else
        {
            return  0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView,mTextViewdate;
        public ImageView mImageViewPhone;
        public RelativeLayout recycler_enquirylist_item;
        public ViewHolder(View v) {
            super(v);
            recycler_enquirylist_item = v.findViewById(R.id.recycler_enquirylist_item);
            this.mTextView = (TextView)v.findViewById(R.id.rv_enquiry_name);
            this.mTextViewdate = (TextView)v.findViewById(R.id.rv_enquiry_date);
            this.mImageViewPhone = (ImageView)v.findViewById(R.id.rv_enquiry_phone);
        }
    }


}
