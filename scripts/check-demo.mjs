import {readFileSync} from "node:fs";
import {runInNewContext} from "node:vm";
import assert from "node:assert/strict";
const html=readFileSync(new URL("../docs/index.html",import.meta.url),"utf8");
const css=readFileSync(new URL("../docs/styles.css",import.meta.url),"utf8");
const js=readFileSync(new URL("../docs/app.js",import.meta.url),"utf8");
for(const id of ["metrics","caseta-grid","quota-grid","license-rows","member-content",
"product-content","detail-dialog","active-role","reset-demo"]){
 assert.ok(html.includes('id="'+id+'"'),"Elemento faltante: "+id);
}
assert.match(html,/GESTIÓN DE FERIA/);
assert.match(css,/grid-template-columns:258px minmax\(0,1fr\)/);
assert.match(css,/@media\(max-width:540px\)/);
assert.doesNotMatch(html,/presentation-title|stage-footer/);
const window={};
runInNewContext(js,{window,console});
const api=window.MrRebujitoDemo;
assert.ok(api,"La API local de pruebas debe estar disponible");
const initial=api.seed();
assert.equal(api.counts(initial).casetas,4);
assert.equal(api.counts(initial).aprobadas,1);
assert.equal(api.counts(initial).pendientes,2);
assert.equal(api.crearSolicitud(initial,1,1,"CASETA").ok,false,"No duplicar aprobadas");
assert.equal(api.crearSolicitud(initial,1,2,"INVITADO").ok,false,"Visitante no puede solicitar");
assert.equal(api.crearSolicitud(initial,1,2,"CASETA").ok,true,"Caseta puede solicitar");
assert.equal(api.crearSolicitud(initial,1,2,"CASETA").ok,false,"No duplicar pendiente");
const sample=api.seed();

assert.equal(api.decidirLicencia(sample,102,"APROBADA","CASETA").ok,false);
assert.equal(api.decidirLicencia(sample,102,"APROBADA","AYUNTAMIENTO").ok,true);
sample.solicitudes.push({id:105,casetaId:3,aytoId:1,estado:"PENDIENTE"});
assert.equal(api.decidirLicencia(sample,105,"APROBADA","AYUNTAMIENTO").ok,false,"No superar cupo");
const socios=api.seed();
socios.casetas[0].socios=[1,2,3,4];
assert.equal(api.anadirSocio(socios,1,5,"CASETA").ok,true,"Última plaza admitida");
assert.equal(api.anadirSocio(socios,1,6,"CASETA").ok,false,"Aforo completo");
assert.equal(api.anadirSocio(socios,2,6,"CASETA").ok,false,"No modificar otra caseta");
const carta=api.seed();
assert.equal(api.anadirProducto(carta,1,"CASETA","Mollete","COMIDA",4.5).ok,true);
assert.equal(api.anadirProducto(carta,1,"CASETA","Producto","COMIDA",-5).ok,false);
assert.equal(api.anadirProducto(carta,2,"CASETA","Producto","BEBIDA",2).ok,false);
console.log("OK: navegación, solicitudes, cupos, aforo y cartas.");
