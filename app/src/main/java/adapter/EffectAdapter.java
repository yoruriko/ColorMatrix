package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gospelware.testcolormatrix.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.Effect;

/**
 * Created by ricogao on 27/05/2016.
 */
public class EffectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Effect> effects;
    private EffectItemClickListener listener;

    public interface EffectItemClickListener {
        void onEffectClick(int position);
    }

    public EffectAdapter(List<Effect> effects, EffectItemClickListener listener) {
        this.effects = effects;
        this.listener = listener;
    }

    class EffectViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.effect_name)
        TextView effectName;

        public EffectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.effect_item_layout, parent, false);
        EffectViewHolder vh = new EffectViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final EffectViewHolder evh = (EffectViewHolder) holder;
        Effect effect = effects.get(position);

        if (effect.getThumbNail() != null) {
            evh.imageView.setImageBitmap(effect.getThumbNail());
        }

        evh.effectName.setText(effect.getName());

        if (listener != null) {
            evh.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEffectClick(evh.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return effects.size();
    }
}
