package com.agibilibus.listaToDo.dao;

import java.util.LinkedList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import model.Tarea;


public class TareaDAO {

	public static boolean select(Tarea tarea){
		boolean result = false;
		
		MongoCollection<Document> collection = MongoBroker.get().getCollection("Tareas");
		
		Document criterio=new Document();
		criterio.append("nombre", tarea.getNombre());
		
		FindIterable<Document> resultado = collection.find(criterio);
		Document tarea_db = resultado.first();
		
		try {
			if (tarea_db!= null) {
				tarea.setAll(((ObjectId)tarea_db.get( "_id" )).toString(), tarea_db.getString( "nombre" ), tarea_db.getString( "done" ));
			    result = true;
				
		}
		}catch(Exception e) {}
		
		return result;
	}
	
	public static boolean selectId(Tarea tarea){
		boolean result = false;
		
		MongoCollection<Document> collection = MongoBroker.get().getCollection("Tareas");
		
		Document criterio=new Document();
		criterio.append("_id", new ObjectId(tarea.getId()) );
		
		FindIterable<Document> resultado = collection.find(criterio);
		Document tarea_bd = resultado.first();
		
		try {
			if (tarea_bd!= null) {
				tarea.setAll(((ObjectId)tarea_bd.get( "_id" )).toString(), tarea_bd.getString( "nombre" ), tarea_bd.getBoolean( "done" ));
				result = true;
				
		}
		}catch(Exception e) {System.out.println("id: "+e.toString());}
		
		return result;
	}
	

	public static List<Tarea> selectAll () {
		
		List<Tarea> result = new LinkedList<Tarea>();

		MongoCollection<Document> collection = MongoBroker.get().getCollection("Tareas");
		MongoCursor<Document> it = collection.find().iterator();
	
		while (it.hasNext()) {
			Document tarea_bd = it.next();
			Tarea tarea = new Tarea(((ObjectId)tarea_bd.get( "_id" )).toString(), tarea_bd.getString( "nombre" ), tarea_bd.getBoolean( "done" ),user_ddbb.getString("nick"));
			result.add(tarea);
		}

		return result;
	}
	public static ObjectId insert(Tarea tarea) {
		Document doc=new Document();
	
		if(tarea.getGroup() == null)
			tarea.setGroup("tareas");
		
		doc.append("nombre", tarea.getNombre());
		doc.append("done", tarea.getDone());

	
		MongoCollection<Document>collection = MongoBroker.get().getCollection("Tareas");
		collection.insertOne(doc);
	
		ObjectId id = (ObjectId)doc.get( "_id" );
		tarea.setId(id.toString());
		
		return id;
	}
	
	public static boolean delete(Tarea tarea) {
		
		MongoCollection<Document> collection = MongoBroker.get().getCollection("Tareas");
		return DAORole.deleteUserRole(tarea.getId()) &&
				collection.deleteOne(new Document("_id", new ObjectId(usuario.getId()))).wasAcknowledged()
				&& DAOGroup.deleteTareaGroupByTarea(tarea.getId());
	
	}
}
