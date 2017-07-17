
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


//2
//#define iterations 17
//#define formuparam 0.53
//
//#define volsteps 10
//#define stepsize 0.1
//
//#define zoom   0.800
//#define tile   0.850
//#define speed  0.010
//
//#define brightness 0.0015
//#define darkmatter 0.300
//#define distfading 0.730
//#define saturation 0.850
//
//
//vec3 mainImage(in vec2 fragCoord )
//{
//	//get coords and direction
//	vec2 uv=fragCoord.xy/u_resolution.xy-.5;
//	uv.y*=u_resolution.y/u_resolution.x;
//	vec3 dir=vec3(uv*zoom,1.);
//	float time=u_time*speed+.25;
//
//	//mouse rotation
//	float a1=.5+u_mouse.x/u_resolution.x*2.;
//	float a2=.8+u_mouse.y/u_resolution.y*2.;
//	mat2 rot1=mat2(cos(a1),sin(a1),-sin(a1),cos(a1));
//	mat2 rot2=mat2(cos(a2),sin(a2),-sin(a2),cos(a2));
//	dir.xz*=rot1;
//	dir.xy*=rot2;
//	vec3 from=vec3(1.,.5,0.5);
//	from+=vec3(time*2.,time,-2.);
//	from.xz*=rot1;
//	from.xy*=rot2;
//
//	//volumetric rendering
//	float s=0.1,fade=1.;
//	vec3 v=vec3(0.);
//	for (int r=0; r<volsteps; r++) {
//		vec3 p=from+s*dir*.5;
//		p = abs(vec3(tile)-mod(p,vec3(tile*2.))); // tiling fold
//		float pa,a=pa=0.;
//		for (int i=0; i<iterations; i++) {
//			p=abs(p)/dot(p,p)-formuparam; // the magic formula
//			a+=abs(length(p)-pa); // absolute sum of average change
//			pa=length(p);
//		}
//		float dm=max(0.,darkmatter-a*a*.001); //dark matter
//		a*=a*a; // add contrast
//		if (r>6) fade*=1.-dm; // dark matter, don't render near
//		//v+=vec3(dm,dm*.5,0.);
//		v+=fade;
//		v+=vec3(s,s*s,s*s*s*s)*a*brightness*fade; // coloring based on distance
//		fade*=distfading; // distance fading
//		s+=stepsize;
//	}
//	v=mix(vec3(length(v)),v,saturation); //color adjust
//	//fragColor = vec4(v*.01,1.);
//
//	return v*0.01;
//}
//
//void main() {
//	//vec2 st = gl_FragCoord.xy/u_resolution;
//	//gl_FragColor = vec4(st.x,st.y,0.0,1.0);
//
//	gl_FragColor = vec4(mainImage(gl_FragCoord.xy),1.);
//
//}




//#########Failed Polygon


//#ifdef GL_ES
//precision highp float;
//#endif
//
//uniform vec2 u_resolution;
//uniform vec2 u_mouse;
//uniform float u_time;
//uniform sampler2D iChannel0;
//varying vec2 texCoord;
//// mi-ku / altair
//
//#define STEPS 50
//#define EPSILON 0.001
////#define FLAT_SHADING
//#define BORDER_LINES
//#define BORDER_THICKNESS 0.25
////#define FLAT_GROUND
//#define TRIANGLE_BG
//
//
//// background
//vec3 shadeBG( vec2 uv, float sp )
//{
//	return vec3( 1.0, 0.95, 0.975 );
//}
//
//int triIsect( const vec3   V1,  // Triangle vertices
//			  const vec3   V2,
//			  const vec3   V3,
//			  const vec3    O,  //Ray origin
//			  const vec3    D,  //Ray direction
//			  out float res )
//{
//  vec3 e1, e2;  //Edge1, Edge2
//  vec3 P, Q, T;
//  float det, inv_det, u, v;
//  float t;
//
//  e1 = V2 - V1;
//  e2 = V3 - V1;
//
//  P = cross( D, e2 );
//  det = dot( e1, P );
//  if(det > -EPSILON && det < EPSILON) return 0;
//  inv_det = 1.0 / det;
//
//  T = O - V1;
//
//  u = dot(T, P) * inv_det;
//  if(u < 0. || u > 1.) return 0;
//
//  Q = cross( T, e1 );
//
//  v = dot(D, Q) * inv_det;
//  if(v < 0. || u + v  > 1.) return 0;
//
//  t = dot(e2, Q) * inv_det;
//
////  if(t > EPSILON) { //ray intersection
////    res = t;
////    return 1;
////  }
//
//  res = t;
//  return 1;
//
//  //return 0;
//}
//int triIsectNC( const vec3   V1,  // Triangle vertices
//			 const vec3   V2,
//			 const vec3   V3,
//			 const vec3    O,  //Ray origin
//			 const vec3    D,  //Ray direction
//			 out float res )
//{
//  vec3 e1, e2;  //Edge1, Edge2
//  vec3 P, Q, T;
//  float det, inv_det, u, v;
//  float t;
//
//  e1 = V2 - V1;
//  e2 = V3 - V1;
//
//  P = cross( D, e2 );
//  det = dot( e1, P );
//  if(det > -EPSILON && det < EPSILON) return 0;
//  inv_det = 1.0 / det;
//
//  T = O - V1;
//
//  u = dot(T, P) * inv_det;
//
//  Q = cross( T, e1 );
//
//  v = dot(D, Q) * inv_det;
//
//  t = dot(e2, Q) * inv_det;
//
//  res = t;
//  return 1;
//
////  if(t > EPSILON ) { //ray intersection
////    res = t;
////    return 1;
////  }
////
////  return 0;
//}
//
//vec3 polygonalGround( vec3 pos, float zshift, float sp )
//{
//	float gridSize = 1.0;
//	pos.z += zshift;
//	vec2 uv1 = floor( pos.xz );
//	vec2 uv2 = uv1 + vec2( gridSize, gridSize );
//	float um = 0.002;
//	float tm = 20.0;
//	float gtm = 5.0;
//
//	float h1 = cos( gtm * u_time/6. + tm * texture2D( iChannel0, um * uv1 ).r );
//	float h2 = cos( gtm * u_time/6. + tm * texture2D( iChannel0, um * vec2( uv2.x, uv1.y ) ).r );
//	float h3 = cos( gtm * u_time/6. + tm * texture2D( iChannel0, um * uv2 ).r );
//	float h4 = cos( gtm * u_time/6. + tm * texture2D( iChannel0, um * vec2( uv1.x, uv2.y ) ).r );
//
//	float hm = 0.7 * max( 0.3, min( 1.0, -( uv1.y - 26.0 ) * 0.05 ) );
//	vec3 v1 = vec3( uv1.x, h1 * hm, uv1.y );
//	vec3 v2 = vec3( uv2.x, h2 * hm, uv1.y );
//	vec3 v3 = vec3( uv2.x, h3 * hm, uv2.y );
//	vec3 v4 = vec3( uv1.x, h4 * hm, uv2.y );
//	float t1, t2, border1, border2;
//	vec3 ro = pos + vec3( 0.0, 100.0, 0.0 );
//	vec3 rd = vec3( 0.0, -1.0, 0.0 );
//	int tri1res = triIsect( v1, v2, v3, ro, rd, t1 );
//	int tri2res = triIsectNC( v1, v3, v4, ro, rd, t2 );
//
//	float h = 0.0;
//	if ( tri1res == 1 )
//	{
//		vec3 pt = ro + rd * t1;
//		return ( pt );
//	}
//	vec3 pt = ro + rd * t2;
//	return pt;
//}
//
//vec3 mapGround( vec3 pos, float sp, float zshift )
//{
//	vec3 res = polygonalGround( pos, zshift, sp );
//	float h = res.y;
//
//	return vec3( pos.x, h * 1.5 - 2.0, pos.z );
//}
//
//float rayMarchGround( vec3 ro, vec3 rd, float sp, float zshift )
//{
//	float t = 0.0;
//	for( int i = 0; i < STEPS; i++ )
//	{
//		vec3 pt = ro + rd * t;
//		float h = ( pt.y - mapGround( pt, sp, zshift ).y );
//		if ( h < -0.515 )
//		{
//			break;
//		}
//		t += 0.3 * h;
//	}
//	return t;
//}
//
//float intersectZPlane( vec3 ro, vec3 rd, float planeZ )
//{
//	return ( -ro.z + planeZ ) / rd.z;
//}
//
//
//// ground
//vec3 shadeGround( vec3 eye, vec3 pt, vec3 norm, vec3 normReflection, vec3 light, float mult, float sp )
//{
//#ifdef FLAT_SHADING
//	pt.xz = pt.xz - mod( pt.xz, 1.0 ); // flat shading
//#endif
//	vec3 r = normalize( reflect( light, norm ) );
//	vec3 eyeDir = normalize( pt  - eye);
//	float dotR = dot( r, eyeDir );
//	float diffuseColor = max( 0.0, dotR );
//	float ambientColor = 0.6;
//	vec3 groundColor = vec3( diffuseColor + ambientColor );
//
//	//vec3 rd = normalize( reflect( -eyeDir, normReflection ) );
//	//float t = intersectZPlane( pt, rd, 10.0 );
//	//vec3 bgPos = pt + rd * t;
//
//	float mixv = max( 1.0, dotR * 2.0 ); //dotR *
//	//vec3 bgColor = shadeBG( abs( bgPos.xy ) * vec2( 0.1, -0.2 ) + vec2( 0.0,2.0 ), sp );
//	return groundColor * mixv ; //+ bgColor * ( 1.0 - mixv )
//}
//
//vec3 colorize( vec2 uv )
//{
//	vec3 ro = vec3( -2.0+ 0.5* sin(u_time), 7.0+ 0.5* sin(u_time), -6.0 + 0.1* sin(u_time)); // 0.  17  -15
//	vec3 rd = vec3( uv.x + 1., uv.y - 6.4, 3.0 ); // -5.4 2.
//	rd = normalize( rd );
//
//	float sp = 0.5; // sound
//
//	float zshift = 0.;
//
//	float t = rayMarchGround( ro, rd, sp, zshift );
//
//	// directional light
//	vec3 lightDir = vec3( sin( u_time/6. ), 0.6, 0.0 );
//	lightDir = normalize( lightDir );
//
//	vec3 color;
//	{
//		vec3 pt = ro + rd * t;
//
//		float eps = 0.0008; //0.001
//		vec3 norm1 = vec3( mapGround( pt + vec3( 0., 0.0, eps ), sp, zshift ).y -
//						   mapGround( pt, sp, zshift ).y,
//						   0.001, //0.005
//						   mapGround( pt + vec3( eps, 0.0, 0. ), sp, zshift ).y -
//						   mapGround( pt, sp, zshift ).y );
//		norm1 = normalize( norm1 );
//		vec3 norm2 = normalize( norm1 + vec3(0.0,8.0,0.0) );
//
//		// border calculation
//		float modx = abs( mod( pt.x, 1.0 ) );
//		float modz = abs( mod( pt.z + zshift, 1.0 ) );
//		float power = 30.0;
//
//        float border = pow(     modx, power )				 // x axis border
//			         + pow( 1.0-modx, power )
//			         + pow(     modz, power )				 // z axis border
//			         + pow( 1.0-modz, power )
//			         + pow( 1.0-abs( modx - modz ), power );              //+ pow( 1.0-abs( modx - modz ), power ); // cross border
//		border = max( 0.0, min( 1.0, border * BORDER_THICKNESS * sp ) );
//
//		vec3 diffuseColor = vec3( 89./255., 168./255., 244./255. );
//		//vec3 diffuseColor = vec3( 1.0, 0.98, .95 );
//        vec3 borderColor = vec3( .1, .1, .1 );
//		vec3 color1 = shadeBG( uv, sp ) * diffuseColor;
//		vec3 color2 = shadeGround( ro, pt, norm1, norm2, lightDir, -1.0, sp ) * diffuseColor * ( 1.0 - border ) + borderColor * border;
//		float mixv = pow( min( min( 1.0, max( 0.0, -abs( pt.x ) + 4.0 ) ), min( 1.0, max( 0.0, -abs( pt.z ) + 4.0 ) ) ), 2.0 );
//		color =  color2 * mixv; //mixv- 0.5  //color1 * ( 1.0 - mixv )
//	}
//
//	return color;
//}
//vec3 noiseGrain( vec2 uv )
//{
//	return vec3(
//		texture2D( iChannel0, uv * 5.0  ).r //+ vec2( u_time * 100.678, u_time * 100.317 )
//	) * 0.2;
//}
//
//vec4 mainImage(  in vec2 fragCoord )
//{
//    vec2 uv = vec2(fragCoord.x / u_resolution.x,fragCoord.y/u_resolution.x * u_resolution.y/u_resolution.x) ;
//	//vec2 uv = fragCoord.xy / u_resolution.xx;
//	uv -= vec2( 0.5, 0.5 );
//	float dist = ( 1.0 - length( uv - vec2( 0.0, 0.25 ) ) * .25) * 1.0;
//	vec3 color = colorize( uv )* dist - noiseGrain( uv ); //* dist
//
//	vec4 fragColor = vec4(color,1.0);
//
//	return fragColor;
//}
//
//void main(){
//    gl_FragColor = vec4(mainImage(gl_FragCoord.xy).xyz,1.);
//}