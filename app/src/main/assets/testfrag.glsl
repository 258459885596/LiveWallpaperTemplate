
#ifdef GL_ES
precision highp float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;
//1
mat2 m(float a){float c=cos(a), s=sin(a);return mat2(c,-s,s,c);}
float map(vec3 p){
    p.xz*= m(u_time*0.4/8.);p.xy*= m(u_time*0.3/8.);
    vec3 q = p*2.+u_time/4. ;
    return length(p+vec3(sin(u_time*0.7/8.)))*log(length(p)+1.) + sin(q.x+sin(q.z+sin(q.y)))*0.5 - 0.75;
}

void main() {
	//vec2 st = gl_FragCoord.xy/u_resolution;
	//gl_FragColor = vec4(st.x,st.y,0.0,1.0);

   vec2 p = gl_FragCoord.xy/u_resolution.y - vec2(0.5-u_mouse.x/2.,0.75+u_mouse.y/2.); // vec2(.5,0.75)
    vec3 cl = vec3(0.);
    float d = 2.5;
    for(int i=0; i<=5; i++)	{
		vec3 p = vec3(0,0,5.+u_mouse/2.+u_mouse.y/2.) + normalize(vec3(p, -2.))*d;
        float rz = map(p);
		float f =  clamp((rz - map(p+.1))*0.5, -.1, 1. );
        vec3 l = vec3(0.1,0.3,.4) + vec3(5., 2.5, 3.)*f;
        cl = cl*l + (1.-smoothstep(0., 2.5, rz))*0.8*l;
		d += min(rz, 5.+u_mouse.x*2.);
	}
    gl_FragColor = vec4(cl, 1.);
}
