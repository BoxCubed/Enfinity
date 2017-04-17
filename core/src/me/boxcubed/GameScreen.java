package me.boxcubed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.CollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDispatcherInfo;
import com.badlogic.gdx.physics.bullet.collision.btManifoldResult;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;

public class GameScreen implements Screen{
	//camera and environment
    public Environment environment;
    public PerspectiveCamera cam;
    public CameraInputController camController;
    
    //models
    public ModelBatch modelBatch;
    public Model model;
    ModelInstance space;
    List<ModelPack> models;
    List<ModelPack> deleteModels=new ArrayList<>();
    //logo
    Texture tex;
    //player model
    ModelPack player;
    //cached asteriod shape for collision
    btCollisionShape asteriodShape;
    //bullet stuff
    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    btBroadphaseInterface broadphase;
    btCollisionWorld collisionWorld;
    Contact contact;
    DebugDrawer collisionDrawer;
    //asset manager
    AssetManager assets;
    //random instance
	Random random = new Random();
	//timer for asteriods
	float elapsedTime = 0;
	//used to determine if player is at max jump height and cannot jump more
	boolean top = false;
	boolean onAndroid=Gdx.app.getType().equals(Application.ApplicationType.Android);
 public GameScreen() {
	 create();
}

    public void create() {
    	loadAssets();
    	initBullet();
    	
    	models=new ArrayList<>();
    	tex=new Texture("logo.png");
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();
        //init of camera
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 0f);
        cam.lookAt(0,0,-50);
        cam.near = 1f;
        cam.far = 3000f;
        cam.update();
        

        //ModelBuilder modelBuilder = new ModelBuilder();
        //to create a gold box with texture on it
       /* model = modelBuilder.createBox(5f, 5f, 5f,
        		new Material(TextureAttribute.createDiffuse(tex))new Material(ColorAttribute.createDiffuse(Color.GOLD)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);*/
        //storing model of asteriod since used a lot
         model=assets.get("models/rock/rock.obj",Model.class);



       

         initPlayer();




     
       ModelPack templateAsteriod=new ModelPack(new ModelInstance(model), new Vector3(0, 4, -100),new Vector3(3, 3, 3), new Quaternion());
       asteriodShape=new btSphereShape(templateAsteriod.bounds.getWidth()/2);
       templateAsteriod.collisionShape=asteriodShape;
       templateAsteriod.collisionOffset=new Vector3(1,-1,-1);
        models.add(templateAsteriod);

        space=new ModelInstance(assets.get("models/spacesphere.obj",Model.class));
        space.transform.scl(3);

        camController = new CameraInputController(cam);


	}

    

	
	void update(float delta){
    	elapsedTime+=delta;
    	camController.update();
    	 collisionWorld.performDiscreteCollisionDetection();
		for (ModelPack model : models){
			
				
			if (!Gdx.input.isKeyPressed(Keys.P) && !Gdx.input.isTouched(3))
				model.location.add(0, 0, 0.7f*delta*100);

    		model.updateTransform();
    		if(model.location.z>5){
    			model.dispose();
    			deleteModels.add(model);}
		}
		models.removeAll(deleteModels);
    	deleteModels.clear();

    	if(elapsedTime>0.03){
    		ModelPack temp=new ModelPack(/*genRandomColorInstance()*/new ModelInstance(model), 
    				new Vector3(random.nextInt(600)-300, 4, random.nextInt(900)-1000), new Vector3(3, 3, 3),new Quaternion());
    		temp.collisionShape=asteriodShape;
    		temp.collisionOffset=new Vector3(1, -1, -1);
    		models.add(temp);
    		
    		elapsedTime=0;
    	}
        handleInput(delta);

    	

    	player.updateTransform();
       
    	
    
    }
	
    @Override
    public void render(float delta) {
    	update(delta);
        

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        modelBatch.begin(cam);
        
        modelBatch.render(player.model,environment);
		for (ModelPack model : models)
			if(isVisible(model))
			modelBatch.render(model.model);


        modelBatch.render(space);
        modelBatch.end();
        collisionDrawer.begin(cam);
        models.forEach(model->{
        	if(model.collisionObject!=null&&isVisible(model))
        	collisionWorld.debugDrawObject(model.collisionObject.getWorldTransform(), model.collisionShape, new Vector3(Color.CYAN.r, Color.CYAN.g, Color.CYAN.b));
        });
        collisionWorld.debugDrawObject(player.collisionObject.getWorldTransform(), player.collisionShape, new Vector3(Color.ORANGE.r, Color.ORANGE.g, Color.ORANGE.b));
        collisionDrawer.end();
    }
    

	private void handleInput(float delta) {
        float roll = 0;
        if (!Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            if (Gdx.input.isKeyPressed(Keys.D) || (Gdx.input.isTouched() && Gdx.input.getX() > Gdx.graphics.getWidth() / 2 && !Gdx.input.isTouched(1))) {

                for (ModelPack model : models) {
                    model.location.x -= 0.7f * delta * 100;
                    model.updateTransform();
                }
                roll = 45;
                space.transform.rotate(Vector3.Y, 1);
            }

        if (Gdx.input.isKeyPressed(Keys.A) || (Gdx.input.isTouched() && Gdx.input.getX() < Gdx.graphics.getWidth() / 2 && !Gdx.input.isTouched(1))) {

            for (ModelPack model : models) {
                model.location.x += 0.7f * delta * 100;
                model.updateTransform();
            }
            roll = -45;
            space.transform.rotate(Vector3.Y, -1);

        }
            player.rotation.setEulerAngles(180, 0, roll);
        } else {//Code for usage of Accelerometer
            player.rotation.setEulerAngles(180, 0, Gdx.input.getAccelerometerY() * 9f);
            for (ModelPack model : models) {
                model.location.x += -Gdx.input.getAccelerometerY() / 10 * delta * 100;
                model.updateTransform();
            }
            space.transform.rotate(Vector3.Y, Gdx.input.getAccelerometerY() / 10 * delta * 100);
        }


        space.transform.rotate(Vector3.X, -0.05f);

        //Code for Jump
        if ((Gdx.input.isKeyPressed(Keys.SPACE) || (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer) && Gdx.input.isTouched(0))
                || Gdx.input.isTouched()
        ) && player.location.y < 10 && !top) {
            player.location.y += 0.7 * delta * 100;

            player.rotation.setEulerAngles(180, -90, roll);
            if (player.location.y >= 10) top = true;
        } else if (player.location.y > 0) {
            player.location.y -= 0.35 * delta * 100;


        } else if (player.location.y <= 0) {
            top = false;
        }
        //cam.position.x = player.location.x;
        player.updateTransform();
        cam.update();
        if (Gdx.input.isKeyJustPressed(Keys.R) || Gdx.input.isTouched(2)) 
        	reset("Restarting...");
            
       

        

    }
    /**
     * @deprecated
     * @param ob
     * @return
     */
   boolean checkCollision(btCollisionObject ob) {
    	  CollisionObjectWrapper co0 = new CollisionObjectWrapper(ob);
          CollisionObjectWrapper co1 = new CollisionObjectWrapper(player.collisionObject);

          btCollisionAlgorithm algorithm = dispatcher.findAlgorithm(co0.wrapper, co1.wrapper);

          btDispatcherInfo info = new btDispatcherInfo();
          btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);

          algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);

          boolean r = result.getPersistentManifold().getNumContacts() > 0;

          dispatcher.freeCollisionAlgorithm(algorithm.getCPointer());
          result.dispose();
          info.dispose();
          co1.dispose();
          co0.dispose();

          return r;
    }
    private void loadAssets(){
    	assets=new AssetManager();
        assets.load("models/ship/ship.g3db", Model.class);
        assets.load("models/spacesphere.obj",Model.class);
        assets.load("models/rock/rock.obj",Model.class);
        assets.finishLoading();
    	
    }
    private void initPlayer() {
    	player=new ModelPack( new ModelInstance(assets.get("models/ship/ship.g3db",Model.class)),
    			new Vector3(0, 0, -20),new Vector3(4, 4, 4), new Quaternion());
    	         player.rotation.setFromAxis(Vector3.Y, 180);
    	       
    	        
    	         Vector3 dimentions=new Vector3();
    	         player.bounds.getDimensions(dimentions);
    	         player.collisionShape=new btBoxShape(dimentions.scl(-0.5f).add(0, 1f, 0));
    	         
    	         player.updateTransform();
    	         player.collisionObject.setUserValue(-1);
		
	}
    private void initBullet() {
    	Bullet.init();
    	collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        collisionWorld = new btCollisionWorld(dispatcher, broadphase, collisionConfig);
        contact=new Contact();
        collisionDrawer=new DebugDrawer();
        collisionDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        collisionWorld.setDebugDrawer(collisionDrawer);
        
		
	}
    private Vector3 position=new Vector3();
    private Vector3 position2=new Vector3();
    private Vector3 temp=new Vector3();
    private boolean isVisible(ModelPack instance) {
    	instance.model.transform.getTranslation(position);
    	instance.bounds.getCenter(position2);
    	position.add(position2);
    	instance.bounds.getDimensions(temp);
    	return cam.frustum.boundsInFrustum(position, temp);
	}
    @Override
    public void dispose() {
        modelBatch.dispose();
        tex.dispose();
        
       assets.dispose();
       models.forEach(model->{
    	   model.dispose();
    	   
       });
       models.clear();
       dispatcher.dispose();
       collisionConfig.dispose();
       asteriodShape.dispose();
       collisionWorld.dispose();
       contact.dispose();
       
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	 ModelInstance genRandomColorInstance(){
		ModelInstance ret=new ModelInstance(model);
		Color at=Color.YELLOW;
		switch(random.nextInt(4)){
		case 0: at=Color.GOLD;
				break;
		case 1: at=Color.RED;
			break;
		case 2: at=Color.BLUE;
		  	break;


		}

		ret.materials.get(0).set(ColorAttribute.createDiffuse(at));

		return ret;
	}
	// int count=0;
	class ModelPack {
		public ModelInstance model;
		public Vector3 location;
		public Quaternion rotation;
		public Vector3 scale = new Vector3();
		public btCollisionShape collisionShape;
		public btCollisionObject collisionObject;
		public Vector3 collisionOffset=new Vector3();
		public BoundingBox bounds;

		public ModelPack(ModelInstance model, Vector3 location,Vector3 scale, Quaternion rotation) {
			super();
			
			this.model = model;
			this.location = location;
			this.rotation = rotation;
			this.scale=scale;
			bounds=new BoundingBox();
			
			
			

			updateTransform();
			model.calculateBoundingBox(bounds);
		}


		


		public void updateTransform() {
			if(collisionObject==null&&collisionShape!=null){
				//count++;
				collisionObject=new btCollisionObject();
				collisionObject.setCollisionShape(collisionShape);
				collisionObject.setWorldTransform(model.transform);
				collisionObject.setUserValue(models.size());
				//collisionObject.setCollisionFlags(collisionObject.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
				collisionWorld.addCollisionObject(collisionObject);
				//System.out.println("registered new object with id: "+models.size());
						}
			model.transform.setToTranslationAndScaling(location, scale);
			model.transform.rotate(rotation);
			if(collisionObject!=null)
			collisionObject.setWorldTransform(model.transform.cpy().translate(collisionOffset));
		}
		public void dispose() {
			if(collisionObject!=null){
		    	   
				collisionWorld.removeCollisionObject(collisionObject);
				collisionObject.dispose();
		    	   }
		    	   /*if(collisionShape!=null)
		    	   collisionShape.dispose();	*/		
		}
	}
	class Contact extends ContactListener {
		
		@Override
		public boolean onContactAdded(int v1, int partId0, int index0, boolean match0, int v2, int partId1,
				int index1, boolean match1) {
			System.out.println("contact!");
			if(v1==-1||v2==-1)GameScreen.this.reset("Crashed!");
			return true;
		}
	}
	public void reset(String mess) {
		if (onAndroid)
            Enfinity.androidAPI.makeToast(mess);
		else System.out.println(mess);


        dispose();
        create();
	}
}
