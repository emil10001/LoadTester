#pragma version(2)
#pragma rs java_package_name(com.feigdev.loadtester)
#include "rs_graphics.rsh"

rs_mesh snowMesh;
typedef struct __attribute__((packed, aligned(4))) Snow {
    float2 velocity;
    float2 position;
    uchar4 color;
} Snow_t;
Snow_t *snow;
float2 wind;
float2 grav;

int root() {
    // Clear to the background color
    rsgClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    // time since last update
    float dt = min(rsGetDt(), 0.1f);
    // dimens
    float w = rsgGetWidth();
    float h = rsgGetHeight();
    int snowCount = rsAllocationGetDimX(rsGetAllocation(snow));
    Snow_t *pSnow = snow;
    for (int i=0; i < snowCount; i++) {
        pSnow->position.x += ((pSnow->velocity.x +wind.x) * dt);
        pSnow->position.y += ((pSnow->velocity.y +wind.y) * dt);
        if (pSnow->position.y > h) {
            pSnow->position.y = 0;
            pSnow->position.x = rsRand(w);
            pSnow->velocity.y = rsRand(60);
        }
        pSnow->velocity.x += (grav.x)*dt;
        pSnow->velocity.y += (grav.y)*dt;
        pSnow++;
    }
    rsgDrawMesh(snowMesh);
    if (rsRand(32) == 1)  {
        wind.x = 0-wind.x;
    }
    return 30;
}

void init() {
    grav.x = 0;
    grav.y = 18;
    wind.x = rsRand(50)+20;
    wind.y = rsRand(4) - 2;
}

void initSnow() {
    const float w = rsgGetWidth();
    const float h = rsgGetHeight();
    int snowCount = rsAllocationGetDimX(rsGetAllocation(snow));
    Snow_t *pSnow = snow;
    for (int i=0; i < snowCount; i++) {
        pSnow->position.x = rsRand(w);
        pSnow->position.y = rsRand(h);
        pSnow->velocity.y = rsRand(60);
        pSnow->velocity.x = rsRand(100);
        pSnow->velocity.x -= 50;
        uchar4 c = rsPackColorTo8888(255, 255, 255);
        pSnow->color = c;
        pSnow++;
    }
}