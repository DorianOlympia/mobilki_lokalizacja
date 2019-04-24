package com.example.mobilki;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.updatePosition();
    }

//    private void RunTri() {
//        double[][] positions = new double[][]{{0,0}, {4, 4}, {4, 0}};
//        double[] distances = new double[]{2.8, 2.8, 2.8};
//
//        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
//        LeastSquaresOptimizer.Optimum optimum = solver.solve();
//
//// the answer
//        double[] centroid = optimum.getPoint().toArray();
//
//// error and geometry information; may throw SingularMatrixException depending the threshold argument provided
//        RealVector standardDeviation = optimum.getSigma(0);
//        RealMatrix covarianceMatrix = optimum.getCovariances(0);
//    }
}
