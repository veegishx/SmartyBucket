package com.saphyrelabs.smartybucket.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.saphyrelabs.smartybucket.R;
import com.saphyrelabs.smartybucket.RecipeDetails;
import com.saphyrelabs.smartybucket.ScanType;
import com.saphyrelabs.smartybucket.model.Item;
import com.saphyrelabs.smartybucket.model.Meal;
import com.saphyrelabs.smartybucket.model.Recipe;
import com.saphyrelabs.smartybucket.model.User;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
    private ArrayList<Recipe> recommendedRecipes = new ArrayList<Recipe>();
    private String [] kitchenIngredientsArray;
    private TextView recommendedRecipeLabel, recRecipeExpense;
    private Button viewRecRecipe, addToBudgetRecRecipe;
    private ProgressBar recipeLoading;

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
        TextView title, ingredients, source, totalPrice;
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

            recipeCard.setOnClickListener(this);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public RecipeAdapter(String userId, String ingredientParameters, List<Recipe> recipes, int rowLayout, Context context, TextView recommendedRecipeLabel, Button viewRecRecipe, Button addToBudgetRecRecipe, TextView recRecipeExpense, ProgressBar recipeLoading) {
        this.userId = userId;
        this.ingredientParameters = ingredientParameters;
        this.recipes = recipes;
        this.rowLayout = rowLayout;
        this.context = context;
        this.recommendedRecipeLabel = recommendedRecipeLabel;
        this.viewRecRecipe = viewRecRecipe;
        this.addToBudgetRecRecipe = addToBudgetRecRecipe;
        this.recRecipeExpense = recRecipeExpense;
        this.recipeLoading = recipeLoading;
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
        ArrayList<String> cleanIngredientLinesArrayList;
        ArrayList<String> cleanRecIngredientLines;
        recipeLoading.setVisibility(VISIBLE);

        viewRecRecipe.setVisibility(View.INVISIBLE);
        addToBudgetRecRecipe.setVisibility(View.INVISIBLE);

        // Based on https://htmlpreview.github.io/?https://github.com/kulsoom-abdullah/kulsoom-abdullah.github.io/blob/master/AWS-lambda-implementation/model_implementation/recipe%20binary%20classification/recipe%20binary%20classification.html#Easy-method-of-removing-%22useless%22-words
        String [] measures = {"litres","liter","millilitres","-ounce","mL","grams","g", "kg","teaspoon", "teaspoons","tsp", "tablespoon", "tablespoons","tbsp", "Tbsp","Tbs","fluid", "ounce","oz","fl.oz", "cup","pint","pt","quart","qt","gallon","gal","smidgen","drop","pinch","dash","scruple","dessertspoon","teacup","Cup","c","pottle","gill","dram","wineglass","coffeespoon","pound","pounds","lb","tbsp","plus","firmly", "packed","lightly","level","even","rounded","heaping","heaped","sifted","bushel","peck","stick","chopped","sliced","halves","shredded","slivered","sliced","whole","paste","whole"," fresh","peeled","diced","mashed","dried","frozen","fresh","peeled","candied","no", "pulp","crystallized","canned","crushed","minced","julienned","clove","head", "small","large","medium"};
        String [] common_remove = {"ground","to","taste", "and", "or", "powder","can","seed","into","cut","grated","leaf","package","finely","divided","a","piece","optional","inch","needed","more","drained","for","flake","juice","dry","breast","extract","yellow","thinly","boneless","skinless","cubed","bell","bunch","cube","slice","pod","beaten","seeded","broth","uncooked","root","plain","baking","heavy","halved","crumbled","sweet","with","hot","confectioner","room","temperature","trimmed","allpurpose","crumb","deveined","bulk","seasoning","jar","food","sundried","italianstyle","if","bag","mix","in","each","roll","instant","double",
                "such","extravirgin","frying","thawed","whipping","stock","rinsed","mild","sprig","brown","freshly","toasted","link","boiling","cooked","basmati","unsalted","container","split",
                "cooking","thin","lengthwise","warm","softened","thick","quartered","juiced","pitted","chunk","melted","cold","coloring","puree","cored","stewed","gingergarlic","floret","coarsely","coarse","the","clarified","blanched","zested","sweetened","powdered","longgrain","garnish","indian","dressing","soup","at","active","french","lean","chip","sour","condensed","long","smoked","ripe","skinned","fillet","flat","from","stem","flaked","removed","zest","stalk","unsweetened","baby","cover","crust","extra","prepared","blend","of","ring","peeled","with","just","the","tops","trimmed","off","about","plus","more","for","drizzling","extra-virgin","roughly","handful","melted","juice"};
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
                        holder.progressBar.setVisibility(GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.thumbnail);

        // Get ingredientLines
        List<String> ingredientsLinesList = recipes.get(position).getIncredientLines();
        // Clean up ingredient lines
        cleanIngredientLinesArrayList = cleanIngredients(ingredientsLinesList, numberLabels, common_remove, measures);

        int totalIngredients = recipes.get(position).getIncredientLines().size();

        holder.title.setText(recipes.get(position).getLabel());

        ArrayList<Double> pricesArraylist = new ArrayList<Double>();

        // Problem with this is, it will match strictly items found in the array.
        // If the array contains an item such as onion[s] and the database contains onion, then it will fail to find a match.
        for (int i = 0; i < cleanIngredientLinesArrayList.size(); i++) {
            int finalI = i;
            smartyFirestore.collection("items")
            .whereEqualTo("itemName", cleanIngredientLinesArrayList.get(i))
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                Double totalItemsPrice = 0.00;
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        recipeLoading.setVisibility(GONE);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get price of ingredient
                            Log.d(TAG, document.getId() + " => " + document.getData().get("itemPrice").toString());
                            pricesArraylist.add(Double.parseDouble(document.getData().get("itemPrice").toString()));
                        }

                        for(Double price : pricesArraylist)
                            totalItemsPrice += price;

                        if (String.valueOf(totalItemsPrice).length() > 4) {
                            holder.totalPrice.setText(String.valueOf(totalItemsPrice).substring(0, 4));
                        } else {
                            holder.totalPrice.setText(String.valueOf(totalItemsPrice));
                        }


                        /**
                         * Add recipe expense to user model and send to Firestore
                         */
                        holder.addToBudgetBtn.setOnClickListener(v -> {
                            System.out.println("PRICE OF EXPENSE:" + holder.totalPrice.getText());
                            DocumentReference docRef = smartyFirestore.collection("users").document(userId);
                            docRef.get().addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful()) {
                                    DocumentSnapshot document = task12.getResult();
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
                                            newMeal.add(new Meal(recipes.get(position).getLabel(), totalItemsPrice, cleanIngredientLinesArrayList, formatter.format(date), recipes.get(position).getUrl()));
                                        } else {
                                            newMeal.add(new Meal(recipes.get(position).getLabel(), totalItemsPrice, cleanIngredientLinesArrayList, formatter.format(date), recipes.get(position).getUrl()));
                                            docRef.update("meals", newMeal);
                                        }

                                        docRef.get().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                User user = document.toObject(User.class);
                                                if (currentExpense != null) {
                                                    user.setExpenses(currentExpense);
                                                } else  {
                                                    user.setExpenses(newExpense);
                                                }

                                                if (meals != null) {
                                                    user.addMeals(new Meal(recipes.get(position).getLabel(), totalItemsPrice, cleanIngredientLinesArrayList, formatter.format(date), recipes.get(position).getUrl()));
                                                } else {
                                                    user.setMeals(newMeal);
                                                }

                                                String id = document.getId();
                                                smartyFirestore.collection("users").document(id).set(user);
                                            }
                                        });


                                        Toast.makeText(context, "Added to budget!", Toast.LENGTH_LONG).show();

                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task12.getException());
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


        String userDietLabel = getMealPreferences();
        System.out.println("MEALPREF: " + userDietLabel);
        for (int j = 0; j < recipes.get(position).getDietLabels().size(); j++) {
            for (int k = 0; k < recipes.get(position).getDietLabels().size(); k++) {
                // Check if recipe contains label set by user, eg: balanced, low-fat, low-carb, high-protein
                if (Pattern.compile(Pattern.quote(recipes.get(position).getDietLabels().get(k)), Pattern.CASE_INSENSITIVE).matcher(userDietLabel).find()) {
                    // Check if user has enough ingredients - difference not more than 4
                    try {
                        if ((kitchenIngredientsArray.length == recipes.get(position).getIncredientLines().size()) || recipes.get(position).getIncredientLines().size() - kitchenIngredientsArray.length < 4) {
                            recommendedRecipes.add(recipes.get(position));
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(context,"Oops, we had some trouble fetching recipes, please try again!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        Collections.shuffle(recommendedRecipes, new Random());
        System.out.println("Size:" + recommendedRecipes.size());
        if (recommendedRecipes.size() > 0) {
            Recipe recommended = recommendedRecipes.get(new Random().nextInt(recommendedRecipes.size()));
            recommendedRecipeLabel.setText(recommended.getLabel());

            viewRecRecipe.setVisibility(VISIBLE);
            addToBudgetRecRecipe.setVisibility(VISIBLE);

            List<String> recommendedRecipeIngredientLines = recommended.getIncredientLines();
            cleanRecIngredientLines = cleanIngredients(recommendedRecipeIngredientLines, numberLabels, common_remove, measures);

            viewRecRecipe.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RecipeDetails.class);

                intent.putExtra("url", recommended.getUrl());
                intent.putExtra("label", recommended.getLabel());
                intent.putExtra("img", recommended.getImage());
                intent.putExtra("source", recommended.getSource());

                v.getContext().startActivity(intent);
            });


            ArrayList<Double> recRecipePricesArraylist = new ArrayList<Double>();
            for (int i = 0; i < cleanRecIngredientLines.size(); i++) {
                int finalI = i;
                smartyFirestore.collection("items")
                .whereEqualTo("itemName", cleanRecIngredientLines.get(i))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    Double totalItemsPrice = 0.00;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get price of ingredient
                                System.out.println("Price of " + cleanRecIngredientLines.get(finalI) + " is " + Double.parseDouble(document.getData().get("itemPrice").toString()));
                                Log.d(TAG, document.getId() + " => " + document.getData().get("itemPrice").toString());
                                recRecipePricesArraylist.add(Double.parseDouble(document.getData().get("itemPrice").toString()));
                            }

                            for(Double price : recRecipePricesArraylist)
                                totalItemsPrice += price;

                            if (String.valueOf(totalItemsPrice).length() > 4) {
                                recRecipeExpense.setText(String.valueOf(totalItemsPrice).substring(0, 4));
                            } else {
                                recRecipeExpense.setText(String.valueOf(totalItemsPrice));
                            }


                            /**
                             * Add recipe expense to user model and send to Firestore
                             */
                            addToBudgetRecRecipe.setOnClickListener(v -> {
                                System.out.println("PRICE OF EXPENSE:" + holder.totalPrice.getText());
                                DocumentReference docRef = smartyFirestore.collection("users").document(userId);
                                docRef.get().addOnCompleteListener(task14 -> {
                                    if (task14.isSuccessful()) {
                                        DocumentSnapshot document = task14.getResult();
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
                                                    float updatedExpense = Float.parseFloat((String) recRecipeExpense.getText()) + Float.parseFloat(currentExpense.get(formatter.format(date)));
                                                    currentExpense.put(formatter.format(date), String.valueOf(updatedExpense));
                                                } else {
                                                    currentExpense.put(formatter.format(date), (String) recRecipeExpense.getText());
                                                }
                                            } else {
                                                newExpense.put(formatter.format(date), (String) recRecipeExpense.getText());
                                                docRef.update("expenses", newExpense);
                                            }

                                            // Check if the meals field exists
                                            if (meals != null) {
                                                newMeal.add(new Meal(recommended.getLabel(), totalItemsPrice, cleanRecIngredientLines, formatter.format(date), recommended.getUrl()));
                                            } else {
                                                newMeal.add(new Meal(recommended.getLabel(), totalItemsPrice, cleanRecIngredientLines, formatter.format(date), recommended.getUrl()));
                                                docRef.update("meals", newMeal);
                                            }

                                            docRef.get().addOnCompleteListener(task13 -> {
                                                if (task13.isSuccessful()) {
                                                    User user = document.toObject(User.class);
                                                    if (currentExpense != null) {
                                                        user.setExpenses(currentExpense);
                                                    } else  {
                                                        user.setExpenses(newExpense);
                                                    }

                                                    if (meals != null) {
                                                        user.addMeals(new Meal(recommended.getLabel(), totalItemsPrice, cleanRecIngredientLines, formatter.format(date), recommended.getUrl()));
                                                    } else {
                                                        user.setMeals(newMeal);
                                                    }


                                                    docRef.set(user);
                                                }
                                            });

                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task14.getException());
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
        } else {
            recommendedRecipeLabel.setText("We couldn't recommend anything at the moment. Try again later!");
            viewRecRecipe.setVisibility(View.INVISIBLE);
            addToBudgetRecRecipe.setVisibility(View.INVISIBLE);
        }


        recommendedRecipeLabel.invalidate();
        recRecipeExpense.invalidate();


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

        int ingredientCount = 0;

        outer: for(int i = 0; i < kitchenIngredientsArray.length; i++) {
            inner: for (int j = 0; j < cleanIngredientLinesArrayList.size(); j++) {
                if (Pattern.compile(Pattern.quote(cleanIngredientLinesArrayList.get(j)), Pattern.CASE_INSENSITIVE).matcher(kitchenIngredientsArray[i]).find()) {
                    System.out.println(cleanIngredientLinesArrayList.get(j));
                    System.out.println(kitchenIngredientsArray[i]);
                    System.out.println(Pattern.compile(Pattern.quote(cleanIngredientLinesArrayList.get(j)), Pattern.CASE_INSENSITIVE).matcher(kitchenIngredientsArray[i]).find());
                    ingredientCount++;
                    break inner;
                }
            }
        }

        holder.ingredients.setText("You have " + ingredientCount + " out of " + totalIngredients + " ingredients");
        holder.recipeCard.setOnClickListener(v -> {

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                Recipe recipe = recipes.get(position);
                String missing = "";
                for (int i = 0; i< kitchenIngredientsArray.length; i++) {
                    for (int j = 0; j < cleanIngredientLinesArrayList.size(); j++) {
                        removeAll(cleanIngredientLinesArrayList, kitchenIngredientsArray[i]);
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.IngredientSummary);
                builder.setTitle("Ingredient Summary");
                // Hardcoding values for now
                builder.setMessage("You are missing the following ingredients: " + cleanIngredientLinesArrayList);
                builder.setPositiveButton("OK", null);
                builder.show();

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
                Log.d("Handler", "Running Handler");
            }, 1000);
        });


    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public ArrayList<String> cleanIngredients(List<String> ingredientsLinesList, String [] numberLabels, String [] common_remove, String [] measures) {
        ArrayList<String> cleanedUpIngredientLinesArrayList = new ArrayList<>();
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
                if (Pattern.compile(Pattern.quote(numberLabelToRemove), Pattern.CASE_INSENSITIVE).matcher(ingredientLine).find()) {
                    String tempWord = numberLabelToRemove + " ";
                    ingredientLine = ingredientLine.replaceAll(tempWord, "");
                }

            }

            // Removing leading and trailing whitespace characters
            ingredientLine = ingredientLine.trim();

            // Removing Common words that make up sentences
            for (String commonWord: common_remove) {
                if (Pattern.compile(Pattern.quote(commonWord), Pattern.CASE_INSENSITIVE).matcher(ingredientLine).find()) {
                    String tempWord = commonWord + " ";
                    ingredientLine = ingredientLine.replaceAll(tempWord, "");
                }

            }

            // Removing leading and trailing whitespace characters
            ingredientLine = ingredientLine.trim();

            // Removing Measure labels
            for (String measureWord: measures) {
                if (Pattern.compile(Pattern.quote(measureWord), Pattern.CASE_INSENSITIVE).matcher(ingredientLine).find()) {
                    String tempWord = measureWord + " ";
                    ingredientLine = ingredientLine.replaceAll(tempWord, "");
                }

            }

            // Removing leading and trailing whitespace characters along with any slash
            ingredientLine = ingredientLine.trim();

            System.out.println("CLEAN: " + ingredientLine);

            cleanedUpIngredientLinesArrayList.add(ingredientLine);
        }

        return cleanedUpIngredientLinesArrayList;

    }

    private String getMealPreferences(){
        String mealPreferences = "";
        Map<String,Boolean> outputMap = new HashMap<String,Boolean>();
        SharedPreferences pSharedPref = RecipeAdapter.this.context.getSharedPreferences("userConfigurations", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("userMealPreferences", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    Boolean value = (Boolean) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        for (Map.Entry<String, Boolean> entry : outputMap.entrySet()) {
            if (entry.getValue() == true) {
                mealPreferences = entry.getKey();
            }
        }
        return mealPreferences;
    }

    public void removeAll(List<String> list, String element) {
        while (list.contains(element)) {
            list.remove(element);
        }
    }
}
