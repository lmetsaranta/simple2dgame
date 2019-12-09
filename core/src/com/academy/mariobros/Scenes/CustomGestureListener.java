package com.academy.mariobros.Scenes;

import com.academy.mariobros.MarioBros;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CustomGestureListener{
    Viewport viewport;
    public Stage stage;
    boolean upPressed, leftPressed, rightPressed;
    OrthographicCamera cam;

    public CustomGestureListener(SpriteBatch batch) {
        cam = new OrthographicCamera();
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, cam);
        stage = new Stage(viewport, batch);

        Gdx.input.setInputProcessor(stage);
    }
}
