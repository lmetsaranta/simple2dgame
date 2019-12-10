package com.academy.mariobros.Sprites;

import com.academy.mariobros.MarioBros;
import com.academy.mariobros.Screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Mario extends Sprite {
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean timeToKillMario;
    private boolean marioIsDead;
    private boolean marioFallToDeath;
    private boolean marioIsHitting;
    public boolean levelPassed;

    private boolean marioHasSword;
    private boolean marioHasSuit;

    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD, HITTING};
    public State previousState;
    public State currentState;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private TextureRegion marioStandWithSword;
    private TextureRegion marioStandWithSuit;
    private Animation marioRun;
    private Animation marioRunWithSword;
    private Animation marioRunWithSuit;
    private TextureRegion marioJump;
    private TextureRegion marioJumpWithSword;
    private TextureRegion marioJumpWithSuit;
    private TextureRegion bigMarioStand;
    private Animation bigMarioRun;
    private TextureRegion bigMarioJump;
    private Animation growMario;
    private TextureRegion marioDead;
    private TextureRegion marioHitWithSword;

    private float stateTime;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;

    public Mario(PlayScreen screen) {
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTime = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        //get animation for little mario to run
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("awmieslapinakyva"), i * 16, 0, 16, 16));
        marioRun = new Animation(0.1f, frames);

        //clear frames for next animation sequence
        frames.clear();

        //get animation for little mario to run with sword
        for(int i = 1; i < 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("awmieslapinakyva"), 112 + (i * 19), 0, 19, 16));
        marioRunWithSword = new Animation(0.1f, frames);

        //clear frames for next animation sequence
        frames.clear();

        //get animation for little mario to run with sword
        for(int i = 0; i < 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("characters"), 119 + (i * 16), 0, 17, 16));
        marioRunWithSuit = new Animation(0.1f, frames);

        //clear frames for next animation sequence
        frames.clear();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("bigawmieslapinakyva"), i * 16 + 1, 0, 16, 32));
        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();

        //get animation frames for growing mario
        frames.add(new TextureRegion(screen.getAtlas().findRegion("bigawmieslapinakyva"), 241, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("bigawmieslapinakyva"), 1, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("bigawmieslapinakyva"), 241, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("bigawmieslapinakyva"), 1, 0, 16, 32));
        growMario = new Animation(0.2f, frames);
        frames.clear();

        //create texture region for little mario standing
        marioStand = new TextureRegion(screen.getAtlas().findRegion("awmieslapinakyva"), 0, 0, 16, 16);
        marioStandWithSword = new TextureRegion(screen.getAtlas().findRegion("awmieslapinakyva"), 112, 0, 19, 16);
        marioStandWithSuit = new TextureRegion(screen.getAtlas().findRegion("characters"), 100, 0, 16, 16);
        marioHitWithSword = new TextureRegion(screen.getAtlas().findRegion("awmieslapinakyva"), 220, 0, 19, 17);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("bigawmieslapinakyva"), 1, 0, 16, 32);
        marioJump = new TextureRegion(screen.getAtlas().findRegion("awmieslapinakyva"), 80, 0, 16, 16);
        marioJumpWithSword = new TextureRegion(screen.getAtlas().findRegion("awmieslapinakyva"), 191, 0, 16, 16);
        marioJumpWithSuit = new TextureRegion(screen.getAtlas().findRegion("characters"), 170, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("bigawmieslapinakyva"), 81, 0, 16, 32);
        marioDead = new TextureRegion(screen.getAtlas().findRegion("awmieslapinakyva"), 96, 0, 16, 16);


        defineMario();

        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStand);
        marioIsBig = false;
        runGrowAnimation = false;
        timeToDefineBigMario = false;
        timeToRedefineMario = false;
        marioIsDead = false;
        marioIsHitting = false;
        marioHasSword = false;
        marioHasSuit = false;
        marioFallToDeath = false;
        timeToKillMario = false;
        levelPassed = false;
    }

    public void update(float dt) {

        if(levelPassed)
            setEndLevel(dt);

        if(b2body.getPosition().y < 10 / MarioBros.PPM && b2body.getPosition().y > 0 / MarioBros.PPM)
            timeToKillMario = true;
        if(marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 );
        setRegion(getFrame(dt));


        if(marioHasSword)
            defineMarioWithSword();
        if(timeToKillMario)
            fallToDeath();
        if(timeToDefineBigMario)
            defineBigMario();
        if(timeToRedefineMario)
            redefineMario();
        if(marioHasSword)
            defineMarioWithSword();
    }

    public void setEndLevel(float dt) {
        if (levelPassed) {
            stateTime += dt;
        }
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = (TextureRegion) growMario.getKeyFrame(stateTime);
                if(growMario.isAnimationFinished(stateTime))
                    runGrowAnimation = false;
                break;
            case HITTING:
                region = marioHitWithSword;
                marioIsHitting = false;
                break;
            case JUMPING:
                region = !marioHasSuit && !marioIsBig && marioHasSword ? marioJumpWithSword : !marioHasSuit && marioIsBig ? bigMarioJump : marioHasSuit ? marioJumpWithSuit : marioJump;
                break;
            case RUNNING:
                region = !marioHasSuit && !marioIsBig && marioHasSword ? (TextureRegion) marioRunWithSword.getKeyFrame(stateTime, true) : !marioHasSuit && marioIsBig ? (TextureRegion) bigMarioRun.getKeyFrame(stateTime, true) : marioHasSuit ? (TextureRegion) marioRunWithSuit.getKeyFrame(stateTime, true) : (TextureRegion) marioRun.getKeyFrame(stateTime, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = !marioHasSuit && !marioIsBig && marioHasSword ? marioStandWithSword : marioIsBig && !marioHasSuit ? bigMarioStand : marioHasSuit ? marioStandWithSuit : marioStand;
                break;
        }

        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;

        return region;
    }

    public State getState() {
        if(marioIsDead || marioFallToDeath)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if(marioIsHitting && marioHasSword)
            return State.HITTING;
        else if(b2body.getLinearVelocity().y > 0 || b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)
            return State.JUMPING;
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.POLE_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public void defineBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.POLE_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void redefineMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.POLE_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToRedefineMario = false;
    }

    public void defineMarioWithSword(){
        Array<Fixture> list = b2body.getFixtureList();

        for(Fixture f : list){
            if(f.getFilterData().categoryBits ==  MarioBros.MARIO_SWORD_BIT){
                b2body.destroyFixture(f);
            }
        }


        FixtureDef fdef = new FixtureDef();
        EdgeShape sword = new EdgeShape();
        if(runningRight && !marioIsHitting){
            sword.set(new Vector2(6 / MarioBros.PPM, 5 / MarioBros.PPM), new Vector2(12 / MarioBros.PPM, 10 / MarioBros.PPM));
        }
        if(runningRight && Gdx.input.isKeyPressed(Input.Keys.X) && stateTime < 0.2f){
            sword.set(new Vector2(6 / MarioBros.PPM, 0 / MarioBros.PPM), new Vector2(14 / MarioBros.PPM, -2 / MarioBros.PPM));
        }
        if(!runningRight && !marioIsHitting){
            sword.set(new Vector2(-6 / MarioBros.PPM, 5 / MarioBros.PPM), new Vector2(-12 / MarioBros.PPM, 10 / MarioBros.PPM));
        }
        if (!runningRight &&  Gdx.input.isKeyPressed(Input.Keys.X) && stateTime < 0.2f){
            sword.set(new Vector2(-6 / MarioBros.PPM, 0 / MarioBros.PPM), new Vector2(-14 / MarioBros.PPM, -2 / MarioBros.PPM));
        }
        fdef.filter.categoryBits = MarioBros.MARIO_SWORD_BIT;
        fdef.shape = sword;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public void grow(){
        if(!marioIsBig) {
            runGrowAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
            MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    public void getSword(){
        if(!marioHasSword){
            marioHasSword = true;
            marioHasSuit = false;
            MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    public void getSuit(){
        if(!marioHasSuit){
            marioHasSuit = true;
            MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();

        }
    }

    public void useSword() {
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            marioIsHitting = true;
        }
        else {
            marioIsHitting = false;
        }
    }

    public boolean isBig() {
        return marioIsBig;
    }

    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle)enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                marioIsDead = true;
                MarioBros.manager.get("audio/music/mariodie.wav", Sound.class).play();
                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList()) {
                    fixture.setFilterData(filter);
                }
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }
    }

    public void fallToDeath(){
        marioFallToDeath = true;
        if(!marioIsDead){
            MarioBros.manager.get("audio/music/mariodie.wav", Sound.class).play();
        }
        timeToKillMario = false;
    }

    public void passTheLevel(){
        levelPassed = true;
        MarioBros.manager.get("audio/music/stageclear.wav", Sound.class).play();
    }

    public boolean isMarioDead(){
        return marioIsDead;
    }

    public float getStateTime(){
        return stateTime;
    }

    public boolean isMarioFallToDeath() {
        return marioFallToDeath;
    }

    public boolean isLevelPassed() {
        return levelPassed;
    }

    public boolean isMarioHasSword() {
        return marioHasSword;
    }
}
