package com.example.android.opengl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Vector;

import android.content.Context;
import android.util.Log;



public class OBJParser {
    int numVertices = 0;
    int numFaces = 0;
    Context context;

    Vector<Short> faces = new Vector<Short>();
    Vector<Short> vtPointer = new Vector<Short>();
    Vector<Short> vnPointer = new Vector<Short>();
    Vector<Float> v = new Vector<Float>();
    Vector<Float> vn = new Vector<Float>();
    Vector<Float> vt = new Vector<Float>();
    //Vector<TDModelPart> parts=new Vector<TDModelPart>();

    public OBJParser(Context ctx) {
        context = ctx;
    }

    /*public static String readTextFileFromRawResource(final Context context,
                                                     final int resourceId)
    {
        final InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }

        return body.toString();
    }*/

    public Dolphin parseOBJ(final int resourceId) {
        String line = null;

        final InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        try {//try to read lines of the file
            while ((line = bufferedReader.readLine()) != null) {
                Log.v("obj", line);
                if (line.startsWith("f")) {//a polygonal face
                    processFLine(line);
                } else if (line.startsWith("vn")) {
                    processVNLine(line);
                } else if (line.startsWith("vt")) {
                    processVTLine(line);
                } else if (line.startsWith("v")) { //line having geometric position of single vertex
                    processVLine(line);
                }
							/*else
								if(line.startsWith("usemtl")){
									try{//start of new group
									if(faces.size()!=0){//if not this is not the start of the first group
										TDModelPart model=new TDModelPart(faces, vtPointer, vnPointer, m,vn);
										parts.add(model);
									}
									String mtlName=line.split("[ ]+",2)[1]; //get the name of the material
									for(int i=0; i<materials.size(); i++){//suppose .mtl file already parsed
										m=materials.get(i);
										if(m.getName().equals(mtlName)){//if found, return from loop
											break;
										}
										m=null;//if material not found, set to null
									}
									faces=new Vector<Short>();
									vtPointer=new Vector<Short>();
									vnPointer=new Vector<Short>();
									}
									catch (Exception e) {
										// TODO: handle exception
									}
								}
								else
									if(line.startsWith("mtllib")){
										materials=MTLParser.loadMTL(line.split("[ ]+")[1]);
										for(int i=0; i<materials.size(); i++){
											Material mat=materials.get(i);
											Log.v("materials",mat.toString());
										}
									}*/
            }
        } catch (IOException e) {
            System.out.println("wtf...");
        }
           /* if(faces!= null){//if not this is not the start of the first group
            TDModelPart model=new TDModelPart(faces, vtPointer, vnPointer, m,vn);
            parts.add(model);*/
        //}
        Dolphin dolphin = new Dolphin(v, vn, vt, faces);
        Log.v("models", dolphin.toString());
        return dolphin;
    }


    private void processVLine(String line) {
        String[] tokens = line.split("[ ]+"); //split the line at the spaces
        int c = tokens.length;
        for (int i = 1; i < c; i++) { //add the vertex to the vertex array
            v.add(Float.valueOf(tokens[i]));
        }
    }

    private void processVNLine(String line) {
        String[] tokens = line.split("[ ]+"); //split the line at the spaces
        int c = tokens.length;
        for (int i = 1; i < c; i++) { //add the vertex to the vertex array
            vn.add(Float.valueOf(tokens[i]));
        }
    }

    private void processVTLine(String line) {
        String[] tokens = line.split("[ ]+"); //split the line at the spaces
        int c = tokens.length;
        for (int i = 1; i < c; i++) { //add the vertex to the vertex array
            vt.add(Float.valueOf(tokens[i]));
        }
    }

    private void processFLine(String line) {
        String[] tokens = line.split("[ ]+");
        int c = tokens.length;

        if (tokens[1].matches("[0-9]+")) {//f: v
            if (c == 4) {//3 faces
                for (int i = 1; i < c; i++) {
                    Short s = Short.valueOf(tokens[i]);
                    s--;
                    faces.add(s);
                }
            } else {//more faces
                Vector<Short> polygon = new Vector<Short>();
                for (int i = 1; i < tokens.length; i++) {
                    Short s = Short.valueOf(tokens[i]);
                    s--;
                    polygon.add(s);
                }
                faces.addAll(Triangulator.triangulate(polygon));//triangulate the polygon and add the resulting faces
            }
        }
        if (tokens[1].matches("[0-9]+/[0-9]+")) {//if: v/vt
            if (c == 4) {//3 faces
                for (int i = 1; i < c; i++) {
                    Short s = Short.valueOf(tokens[i].split("/")[0]);
                    s--;
                    faces.add(s);
                    s = Short.valueOf(tokens[i].split("/")[1]);
                    s--;
                    vtPointer.add(s);
                }
            } else {//triangulate
                Vector<Short> tmpFaces = new Vector<Short>();
                Vector<Short> tmpVt = new Vector<Short>();
                for (int i = 1; i < tokens.length; i++) {
                    Short s = Short.valueOf(tokens[i].split("/")[0]);
                    s--;
                    tmpFaces.add(s);
                    s = Short.valueOf(tokens[i].split("/")[1]);
                    s--;
                    tmpVt.add(s);
                }
                faces.addAll(Triangulator.triangulate(tmpFaces));
                vtPointer.addAll(Triangulator.triangulate(tmpVt));
            }
        }
        if (tokens[1].matches("[0-9]+//[0-9]+")) {//f: v//vn
            if (c == 4) {//3 faces
                for (int i = 1; i < c; i++) {
                    Short s = Short.valueOf(tokens[i].split("//")[0]);
                    s--;
                    faces.add(s);
                    s = Short.valueOf(tokens[i].split("//")[1]);
                    s--;
                    vnPointer.add(s);
                }
            } else {//triangulate
                Vector<Short> tmpFaces = new Vector<Short>();
                Vector<Short> tmpVn = new Vector<Short>();
                for (int i = 1; i < tokens.length; i++) {
                    Short s = Short.valueOf(tokens[i].split("//")[0]);
                    s--;
                    tmpFaces.add(s);
                    s = Short.valueOf(tokens[i].split("//")[1]);
                    s--;
                    tmpVn.add(s);
                }
                faces.addAll(Triangulator.triangulate(tmpFaces));
                vnPointer.addAll(Triangulator.triangulate(tmpVn));
            }
        }
        if (tokens[1].matches("[0-9]+/[0-9]+/[0-9]+")) {//f: v/vt/vn

            if (c == 4) {//3 faces
                for (int i = 1; i < c; i++) {
                    Short s = Short.valueOf(tokens[i].split("/")[0]);
                    s--;
                    faces.add(s);
                    s = Short.valueOf(tokens[i].split("/")[1]);
                    s--;
                    vtPointer.add(s);
                    s = Short.valueOf(tokens[i].split("/")[2]);
                    s--;
                    vnPointer.add(s);
                }
            } else {//triangulate
                Vector<Short> tmpFaces = new Vector<Short>();
                Vector<Short> tmpVn = new Vector<Short>();
                //Vector<Short> tmpVt=new Vector<Short>();
                for (int i = 1; i < tokens.length; i++) {
                    Short s = Short.valueOf(tokens[i].split("/")[0]);
                    s--;
                    tmpFaces.add(s);
                    //s=Short.valueOf(tokens[i].split("/")[1]);
                    //s--;
                    //tmpVt.add(s);
                    //s=Short.valueOf(tokens[i].split("/")[2]);
                    //s--;
                    //tmpVn.add(s);
                }
                faces.addAll(Triangulator.triangulate(tmpFaces));
                vtPointer.addAll(Triangulator.triangulate(tmpVn));
                vnPointer.addAll(Triangulator.triangulate(tmpVn));
            }
        }
    }
}

