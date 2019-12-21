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

import in.weoto.gymoto.GetterSetter.Member;
import in.weoto.gymoto.R;
import in.weoto.gymoto.SingleMemberActivity;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    ArrayList<Member> members;
    Context context;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public MemberAdapter(ArrayList<Member> members, Context context) {
        this.members = members;
        this.context = context;
    }

    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_memebers_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberAdapter.ViewHolder holder, final int position) {
        holder.mTextView.setText(members.get(position).getName());
        holder.mTextViewRemaining.setText(members.get(position).getRemainingAmt());

        holder.mImageViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = members.get(position).getPhone();
                if(phone.equals("")|| phone ==null) {
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

        holder.recycler_member_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(context, members.get(position).getMemID(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,SingleMemberActivity.class);
                intent.putExtra("memberID", members.get(position).getMemID());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        if(members != null) {
            return members.size();
        }
        else
        {
            return  0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView,mTextViewRemaining;
        public ImageView mImageViewPhone;
        public RelativeLayout recycler_member_item;
        public ViewHolder(View v) {
            super(v);
            recycler_member_item = v.findViewById(R.id.recycler_member_item);
            this.mTextView = (TextView)v.findViewById(R.id.rv_members_name);
            this.mTextViewRemaining = (TextView)v.findViewById(R.id.rv_remaining_amount);
            this.mImageViewPhone = (ImageView)v.findViewById(R.id.rv_phone);



        }
    }


}
