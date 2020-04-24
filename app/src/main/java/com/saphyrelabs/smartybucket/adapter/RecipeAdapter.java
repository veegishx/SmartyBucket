package com.saphyrelabs.smartybucket.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.saphyrelabs.smartybucket.R;
import com.saphyrelabs.smartybucket.RecipeDetails;
import com.saphyrelabs.smartybucket.model.Item;
import com.saphyrelabs.smartybucket.model.Meal;
import com.saphyrelabs.smartybucket.model.Recipe;
import com.saphyrelabs.smartybucket.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final String ingredientParameters;
    private final String userId;
    private List<Recipe> recipes;
    private Context context;
    private int rowLayout;
    private OnItemClickListener onItemClickListener;
    private FirebaseFirestore smartyFirestore;
    private static final String TAG = "RecipeAdapterFirestore";
    private Map<String, String> newExpense = new HashMap<>();
    private ArrayList<Meal> newMeal = new ArrayList<Meal>();
    private int ingredientsInPossessionCount = 0;
    private String [] kitchenIngredientsArray;

    private void initFirestore() {
        smartyFirestore = FirebaseFirestore.getInstance();
    }

    private void queryIngredientInFirestore(String ingredient) {
        int count = 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, ingredients, source, totalPrice, recommendedRecipeLabel;
        ImageView thumbnail;
        ProgressBar progressBar;
        OnItemClickListener onItemClickListener;
        FrameLayout recipesLayout;
        Button viewRecipeBtn, addToBudgetBtn;
        CardView recipeCard;

        public RecipeViewHolder(View v, OnItemClickListener onItemClickListener) {

            super(v);
            recipeCard = (CardView) v.findViewById(R.id.recipeCard);
            recipesLayout = (FrameLayout) v.findViewById(R.id.recipes_layout);
            title = (TextView) v.findViewById(R.id.recipe_title);
            ingredients = (TextView) v.findViewById(R.id.recipe_ingredients);
            thumbnail = (ImageView) v.findViewById(R.id.recipe_thumbnail);
            progressBar =(ProgressBar) v.findViewById(R.id.progress_load_photo);
            viewRecipeBtn = (Button) v.findViewById(R.id.viewRecipeBtn);
            addToBudgetBtn = (Button) v.findViewById(R.id.addToBudgetBtn);
            totalPrice = (TextView) v.findViewById(R.id.totalPrice);
            recommendedRecipeLabel = (TextView) v.findViewById(R.id.recommendedRecipeLabel);

            recipeCard.setOnClickListener(this);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public RecipeAdapter(String userId, String ingredientParameters, List<Recipe> recipes, int rowLayout, Context context) {
        this.userId = userId;
        this.ingredientParameters = ingredientParameters;
        this.recipes = recipes;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new RecipeViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, final int position) {
        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        ArrayList<String> cleanIngredientLines = new ArrayList<String>();
        ArrayList<Item> dbItemList = new ArrayList<>();

        // Based on https://htmlpreview.github.io/?https://github.com/kulsoom-abdullah/kulsoom-abdullah.github.io/blob/master/AWS-lambda-implementation/model_implementation/recipe%20binary%20classification/recipe%20binary%20classification.html#Easy-method-of-removing-%22useless%22-words
        String [] measures = {"litres","liter","millilitres","-ounce","mL","grams","g", "kg","teaspoon", "teaspoons","tsp", "tablespoon", "tablespoons","tbsp", "Tbsp","Tbs","fluid", "ounce","oz","fl.oz", "cup","pint","pt","quart","qt","gallon","gal","smidgen","drop","pinch","dash","scruple","dessertspoon","teacup","cup","c","pottle","gill","dram","wineglass","coffeespoon","pound","pounds","lb","tbsp","plus","firmly", "packed","lightly","level","even","rounded","heaping","heaped","sifted","bushel","peck","stick","chopped","sliced","halves","shredded","slivered","sliced","whole","paste","whole"," fresh","peeled","diced","mashed","dried","frozen","fresh","peeled","candied","no", "pulp","crystallized","canned","crushed","minced","julienned","clove","head", "small","large","medium"};
        String [] common_remove = {"ground","to","taste", "and", "or", "powder","can","seed","into","cut","grated","leaf","package","finely","divided","a","piece","optional","inch","needed","more","drained","for","flake","juice","dry","breast","extract","yellow","thinly","boneless","skinless","cubed","bell","bunch","cube","slice","pod","beaten","seeded","broth","uncooked","root","plain","baking","heavy","halved","crumbled","sweet","with","hot","confectioner","room","temperature","trimmed","allpurpose","crumb","deveined","bulk","seasoning","jar","food","sundried","italianstyle","if","bag","mix","in","each","roll","instant","double",
                "such","extravirgin","frying","thawed","whipping","stock","rinsed","mild","sprig","brown","freshly","toasted","link","boiling","cooked","basmati","unsalted","container","split",
                "cooking","thin","lengthwise","warm","softened","thick","quartered","juiced","pitted","chunk","melted","cold","coloring","puree","cored","stewed","gingergarlic","floret","coarsely","coarse","the","clarified","blanched","zested","sweetened","powdered","longgrain","garnish","indian","dressing","soup","at","active","french","lean","chip","sour","condensed","long","smoked","ripe","skinned","fillet","flat","from","stem","flaked","removed","zest","stalk","unsweetened","baby","cover","crust","extra","prepared","blend","of","ring","peeled","with","just","the","tops","trimmed","off","about","plus","more","for","drizzling","extra-virgin","roughly","handful"};
        String [] numberLabels = {"1","2","3","4","5","6","7","8","9","\\/"};

        initFirestore();

        Recipe model = recipes.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(model.getImage())
                .apply(requestOptions)
                .placeholder(R.drawable.ic_local_dining_black_24dp)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.thumbnail);


        // Get ingredientLines
        List<String> ingredientsLinesList = recipes.get(position).getIncredientLines();
        for (String ingredientLine: ingredientsLinesList) {

            System.out.println("DIRTY: " + ingredientLine);

            // Removing anything after comma, which are usually specifics about the ingredient, therefore we can safely discard this portion of the string
            ingredientLine = ingredientLine.split(",")[0];

            // Removing any parenthesis
            ingredientLine = ingredientLine.replaceAll("\\(.*\\)", "");

            // Removing Unicode symbols
            ingredientLine = ingredientLine.replaceAll("\\p{No}+", "");

            // Removing any dots and digits
            ingredientLine = ingredientLine.replaceAll("[^a-zA-Z]"," ");

            // Removing leading and trailing whitespace characters
            ingredientLine = ingredientLine.trim();

            // Removing fractions represented using slash instead of unicode characters
            for (String numberLabelToRemove: numberLabels) {
                if (ingredientLine.contains(numberLabelToRemove)) {
                    String tempWord = numberLabelToRemove + " ";
                    ingredientLine = ingredientLine.replaceAll(tempWord, "");
                }

            }

            // Removing leading and trailing whitespace characters
            ingredientLine = ingredientLine.trim();

            // Removing Common words that make up sentences
            for (String commonWord: common_remove) {
                if (ingredientLine.contains(commonWord)) {
                    String tempWord = commonWord + " ";
                    ingredientLine = ingredientLine.replaceAll(tempWord, "");
                }

            }

            // Removing leading and trailing whitespace characters
            ingredientLine = ingredientLine.trim();

            // Removing Measure labels
            for (String measureWord: measures) {
                if (ingredientLine.contains(measureWord)) {
                    String tempWord = measureWord + " ";
                    ingredientLine = ingredientLine.replaceAll(tempWord, "");
                }

            }

            // Removing leading and trailing whitespace characters along with any slash
            ingredientLine = ingredientLine.trim();;

            System.out.println("CLEAN: " + ingredientLine);

            cleanIngredientLines.add(ingredientLine);
        }

        int totalIngredients = recipes.get(position).getIncredientLines().size();

        holder.title.setText(recipes.get(position).getLabel());

        ArrayList<Double> pricesArraylist = new ArrayList<Double>();

        for (int i = 0; i < cleanIngredientLines.size(); i++) {
            int finalI = i;
            smartyFirestore.collection("items")
                    .whereEqualTo("itemName", cleanIngredientLines.get(i))
                    .limit(20)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        Double totalItemsPrice = 0.00;
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Get price of ingredient
                                    System.out.println("Price of " + cleanIngredientLines.get(finalI) + " is " + Double.parseDouble(document.getData().get("itemPrice").toString()));
                                    //totalItemsPrice += Double.parseDouble(document.getData().get("itemPrice").toString());
                                    Log.d(TAG, document.getId() + " => " + document.getData().get("itemPrice").toString());
                                    pricesArraylist.add(Double.parseDouble(document.getData().get("itemPrice").toString()));
                                }
//                                holder.totalPrice.setText(String.valueOf(totalItemsPrice));
                                System.out.println("PricesArrayList: " + pricesArraylist.size());
                                for(Double price : pricesArraylist)
                                    totalItemsPrice += price;

                                holder.totalPrice.setText(String.valueOf(totalItemsPrice));

                                /**
                                 * Add recipe expense to user model and send to Firestore
                                 */
                                holder.addToBudgetBtn.setOnClickListener(v -> {
                                    System.out.println("PRICE OF EXPENSE:" + holder.totalPrice.getText());
                                    DocumentReference docRef = smartyFirestore.collection("users").document(userId);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    // LOGGING EVENTS
                                                    Log.d(TAG, "DocumentSnapshot data: " + document.get("expenses"));
                                                    Log.d(TAG, "DocumentSnapshot data: " + document.get("meals"));

                                                    // Retrieving expense data from Firestore
                                                    Map<String, String> currentExpense = (HashMap<String, String>) document.get("expenses");
                                                    // Retrieving meal data from Firestore
                                                    ArrayList<Meal> meals = (ArrayList<Meal>) document.get("meals");

                                                    // Creating a new instance of the Date object
                                                    Date date = new Date();
                                                    // Formatting current date to day/month/year
                                                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                                                    // Check if the expenses field exist
                                                    if (currentExpense != null){
                                                        if (currentExpense.get(formatter.format(date)) != null) {
                                                            float updatedExpense = Float.parseFloat((String) holder.totalPrice.getText()) + Float.parseFloat(currentExpense.get(formatter.format(date)));
                                                            currentExpense.put(formatter.format(date), String.valueOf(updatedExpense));
                                                        } else {
                                                            currentExpense.put(formatter.format(date), (String) holder.totalPrice.getText());
                                                        }
                                                    } else {
                                                        newExpense.put(formatter.format(date), (String) holder.totalPrice.getText());
                                                        docRef.update("expenses", newExpense);
                                                    }

                                                    // Check if the meals field exists
                                                    if (meals != null) {
                                                        newMeal.add(new Meal(recipes.get(position).getLabel(), totalItemsPrice, cleanIngredientLines));
                                                    } else {
                                                        newMeal.add(new Meal(recipes.get(position).getLabel(), totalItemsPrice, cleanIngredientLines));
                                                        docRef.update("meals", newMeal);
                                                    }

                                                    smartyFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (DocumentSnapshot document : task.getResult()) {
                                                                    User user = document.toObject(User.class);
                                                                    if (currentExpense != null) {
                                                                        user.setExpenses(currentExpense);
                                                                    } else  {
                                                                        user.setExpenses(newExpense);
                                                                    }

                                                                    if (meals != null) {
                                                                        user.addMeals(new Meal(recipes.get(position).getLabel(), totalItemsPrice, cleanIngredientLines));
                                                                    } else {
                                                                        user.setMeals(newMeal);
                                                                    }

                                                                    String id = document.getId();
                                                                    smartyFirestore.collection("users").document(id).set(user);
                                                                }
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    Log.d(TAG, "No such document");
                                                }
                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                });

                                System.out.println("TOTAL PRICE:" + totalItemsPrice);
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }


        holder.viewRecipeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetails.class);

            Recipe recipe = recipes.get(position);
            intent.putExtra("url", recipe.getUrl());
            intent.putExtra("label", recipe.getLabel());
            intent.putExtra("img", recipe.getImage());
            intent.putExtra("source", recipe.getSource());

            v.getContext().startActivity(intent);
        });

        kitchenIngredientsArray = ingredientParameters.replaceAll("\\s+","").split(",");

//        for (int i = 0; i < cleanIngredientLines.size(); i++) {
//            for (int j = 0; j < kitchenIngredientsArray.length; j++) {
//                if (cleanIngredientLines.get(i).equalsIgnoreCase(kitchenIngredientsArray[j])) {
//                    ingredientCount++;
//                    break;
//                }
//            }
//        }


        int ingredientCount = 0;

        for(int i = 0; i < cleanIngredientLines.size(); i++) {
            for (int j = 0; j < kitchenIngredientsArray.length; j++) {
                if (cleanIngredientLines.get(i).equalsIgnoreCase(kitchenIngredientsArray[j])) {
                    System.out.println(cleanIngredientLines.get(i));
                    System.out.println(kitchenIngredientsArray[j]);
                    System.out.println(cleanIngredientLines.get(i).equalsIgnoreCase(kitchenIngredientsArray[j]));
                    ingredientCount++;
                    System.out.println("--------------");
                }
            }
        }

        holder.ingredients.setText("You have " + ingredientCount + " out of " + totalIngredients + " ingredients");
        System.out.println("Ingredient count for " + recipes.get(position).getLabel() + " is " + ingredientCount);


        holder.recipeCard.setOnClickListener(v -> {
            Recipe recipe = recipes.get(position);
            System.out.println("Tapped!");
            System.out.println(recipe.getIncredientLines().toString());
            System.out.println(ingredientParameters);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.IngredientSummary);
                    builder.setTitle("Ingredient Summary");
                    // Hardcoding values for now
                    builder.setMessage("You are missing the following ingredients: Onions, Black Pepper, Tomatoes. Total price is: ");
                    builder.setPositiveButton("OK", null);
                    builder.show();

                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //totalCost[0] = 0.0;
                        }
                    });
                    Log.d("Handler", "Running Handler");
                }
            }, 1000);
        });


    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
