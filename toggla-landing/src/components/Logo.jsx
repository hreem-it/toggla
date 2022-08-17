import Image from 'next/future/image'
import logo from '@/images/logos/toggla-logo-transparent.png'

export function Logo(_) {
  return (
    <Image
            className="w-40"
            src={logo}
            alt=""
            unoptimized
          />
  )
}
