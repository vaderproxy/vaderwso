package base;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.python.core.PyDictionary;
import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyString;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import struct.WSOQuery;

public class JPython {

    private static HashMap<String, String> convertMap(ConcurrentHashMap<PyObject, PyObject> tmp_map) {
        HashMap<String, String> result = new HashMap();
        for (Map.Entry<PyObject, PyObject> entry : tmp_map.entrySet()) {
            PyObject key = entry.getKey();
            PyObject value = entry.getValue();

            result.put(key.asString(), value.asString());

        }

        return result;
    }

    public static WSOQuery get_query(String pyFile, String url, String code) {
        WSOQuery query = new WSOQuery();
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile("./pyconfig/" + pyFile);
        PyFunction fnc = (PyFunction) interpreter.get("wso_query", PyFunction.class);
        PyDictionary result = (PyDictionary) fnc.__call__(new PyString(url), new PyString(code));
        try {
            PyDictionary post = (PyDictionary) result.get("post");
            HashMap<String, String> tmp_map = new HashMap();
            tmp_map.putAll(convertMap((ConcurrentHashMap<PyObject, PyObject>) post.getMap()));

            query.post = tmp_map;
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        try {
            PyDictionary cookie = (PyDictionary) result.get("cookie");
            HashMap<String, String> tmp_map = new HashMap();
            tmp_map.putAll(convertMap((ConcurrentHashMap<PyObject, PyObject>) cookie.getMap()));
            query.cookie = tmp_map;
        } catch (Exception ex) {
        }

        try {
            query.url = (String) result.get("url");
        } catch (Exception ex) {
        }

        interpreter.close();

        return query;
    }

}
