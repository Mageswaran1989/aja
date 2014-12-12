#include <stdint.h>

enum {
  ENDIAN_UNKNOWN = 0,
  ENDIAN_BIG,
  ENDIAN_LITTLE,
  ENDIAN_BIG_WORD,   /* Middle-endian, Honeywell 316 style */
  ENDIAN_LITTLE_WORD /* Middle-endian, PDP-11 style */
};

int endianness(void)
{
  uint32_t value;
  uint8_t *buffer = (uint8_t *)&value;

  buffer[0] = 0x00;
  buffer[1] = 0x01;
  buffer[2] = 0x02;
  buffer[3] = 0x03;

  switch (value)
  {
  case UINT32_C(0x00010203): return ENDIAN_BIG;
  case UINT32_C(0x03020100): return ENDIAN_LITTLE;
  case UINT32_C(0x02030001): return ENDIAN_BIG_WORD;
  case UINT32_C(0x01000302): return ENDIAN_LITTLE_WORD;
  default:                   return ENDIAN_UNKNOWN;
  }
}
