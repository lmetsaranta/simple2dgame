package com.academy.mariobros.Screens;

import com.academy.mariobros.Items.Sword;
import com.academy.mariobros.Scenes.Controller;
import com.academy.mariobros.Items.Item;
import com.academy.mariobros.Items.ItemDef;
import com.academy.mariobros.Items.Mushroom;
import com.academy.mariobros.MarioBros;
import com.academy.mariobros.Scenes.GestureController;
import com.academy.mariobros.Scenes.Hud;
import com.academy.mariobros.Sprites.Enemy;
import com.academy.mariobros.Sprites.Mario;
import com.academy.mariobros.Tools.B2WorldCreator;
import com.academy.mariobros.Tools.WorldContactListener;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    //Reference to our Game, used to set Screens
    private MarioBros games;
    private TextureAtlas atlas;

    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    //Tiled map variables
    private TmxMapLoader maploader;
    private ArrayList<TiledMap> maps;
    private TiledMap map1;
    private TiledMap map2;
    private TiledMap map;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator level;


    private Controller controller;
    private GestureController gestureController;

    public PlayScreen(MarioBros game) {

        this.games = game;

        this.atlas = games.getAtlas2();

        gamecam = new OrthographicCamera();

        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);

        hud = new Hud(games.batch);

        this.maps = new ArrayList<TiledMap>();
        this.maploader = new TmxMapLoader();
        this.map1 = new TiledMap();
        this.map2 = new TiledMap();
        map1 = maploader.load("originalmap.tmx");
        map2 = maploader.load("originalmap2.tmx");
        maps.add(map1);
        maps.add(map2);

        this.map = maps.get(games.getLevel());

        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);

        //allows for debug lines of our box2d world
        b2dr = new Box2DDebugRenderer();

        this.level = new B2WorldCreator(this);

        //create Mario in our games world
        player = new Mario(this);


        world.setContactListener(new WorldContactListener());

        music = MarioBros.manager.get("audio/music/maintheme.mp3",Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

        controller = new Controller(games.batch);
        gestureController = new GestureController(games.batch);
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            } else if(idef.type == Sword.class){
                items.add(new Sword(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }


    @Override
    public void show() {

    }

    public void handleInput(float dt) {

        if(Gdx.app.getType() != Application.ApplicationType.Android) {
            if (player.currentState != Mario.State.DEAD && !player.isLevelPassed()) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.b2body.getLinearVelocity().y == 0)
                    player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                    player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
                    player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
                if (Gdx.input.isKeyPressed(Input.Keys.X) && player.isMarioHasSword())
                    player.useSword();
            }
        } else {
            if (player.currentState != Mario.State.DEAD) {
                if (gestureController.isUpPressed() && player.b2body.getLinearVelocity().y == 0) {
                    player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
                    gestureController.setUpPressed(false);
                }
                if(Gdx.input.isTouched() && player.b2body.getLinearVelocity().x <= 2){
                    if(Gdx.input.getX() > 1020) {
                        player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
                    }
                }
                if(Gdx.input.isTouched() && player.b2body.getLinearVelocity().x >= -2){
                    if(Gdx.input.getX() < 1020) {
                        player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
                    }
                }
            }

//            if (player.currentState != Mario.State.DEAD && !player.isLevelPassed()) {
//                if (controller.isUpPressed() && player.b2body.getLinearVelocity().y == 0)
//                    player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
//                if(controller.isRightPressed() && player.b2body.getLinearVelocity().x <= 2)
//                    player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
//                if(controller.isLeftPressed() && player.b2body.getLinearVelocity().x >= -2)
//                    player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
//            }
        }
    }

    public void update(float dt) {
        //handle user input first
        handleInput(dt);
        handleSpawningItems();

        world.step(1/60f, 6, 2);

        player.update(dt);
        for(Enemy enemy : level.getEnemies())
            enemy.update(dt);

        for(Item item : items)
            item.update(dt);

        hud.update(dt);

        if(player.isMarioDead() || player.isMarioFallToDeath() || player.isLevelPassed()) {
            music.stop();
        }

        if(player.currentState != Mario.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
        }

        gamecam.update();
        level.getRenderer().setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);

        //Clear the games screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render our games map
        level.getRenderer().render();

        //render our Box2dDebugLines
        b2dr.render(world, gamecam.combined);

        games.batch.setProjectionMatrix(gamecam.combined);
        games.batch.begin();
        player.draw(games.batch);
        for(Enemy enemy : level.getEnemies())
            enemy.draw(games.batch);
        for(Item item : items)
            item.draw(games.batch);
        games.batch.end();

        //Set our batch to now draw what the Hud camera sees
        games.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(Gdx.app.getType() == Application.ApplicationType.Android){
            controller.draw();
        }

        if(gameOver()){
            games.setScreen(new GameOverScreen(games));
            dispose();
        }

        if(levelPassed()){
            games.setScreen(new LevelPassedScreen(games));
            dispose();
        }
    }

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTime() > 3){
            return true;
        } else {
            return false;
        }
    }

    public boolean levelPassed(){
        if(player.levelPassed && player.getStateTime() > 3){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        controller.resize(width, height);
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        level.getRenderer().dispose();
        map.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
