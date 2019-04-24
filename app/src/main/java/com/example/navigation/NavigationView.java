package com.example.navigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.model.Room;
import com.example.model.Router;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

public class NavigationView extends View {
    private Room room;
    private Paint wallPaint;
    private Paint routerPaint;
    private Paint phonePaint;

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        room = new Room(10, 10);

        wallPaint = new Paint();
        wallPaint.setColor(Color.BLUE);
        wallPaint.setStrokeWidth(0.5f);
        wallPaint.setStyle(Paint.Style.STROKE);

        routerPaint = new Paint();
        routerPaint.setStrokeWidth(0.5f);
        routerPaint.setColor(Color.BLACK);

        phonePaint = new Paint();
        phonePaint.setStrokeWidth(0.5f);
        phonePaint.setColor(Color.CYAN);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(90, 90);
//        canvas.translate(mPosX, mPosY);
        drawWalls(canvas);
        drawRouters(canvas);
        drawPosition(canvas);

        canvas.restore();
    }

    public void updatePosition(){
        invalidate();
    }


    public void drawWalls(Canvas canvas) {
        canvas.drawRect(0, 0, room.getxLengthMeters(), room.getyLengthMeters(), wallPaint);
    }

    public void drawRouters(Canvas canvas){
        for(Router router : room.getRouterList()){
            canvas.drawPoint(router.getxPosMeters(), router.getyPosMeters(), routerPaint);
        }
    }

    public void drawPosition(Canvas canvas){
        double[] routerDistances = new double[room.getRouterList().size()];
        double[][] routerPositions = new double[room.getRouterList().size()][2];
        for(int i = 0;i<room.getRouterList().size();i++){
            routerDistances[i]= (double) room.getRouterList().get(i).getMockedDistance();
            routerPositions[i][0]= (double) room.getRouterList().get(i).getxPosMeters();
            routerPositions[i][1]= (double) room.getRouterList().get(i).getyPosMeters();

        }

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(routerPositions,routerDistances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();
        double[] centroid = optimum.getPoint().toArray();
        canvas.drawPoint((float)centroid[0], (float)centroid[1], phonePaint);
    }
}
